package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AttributeUpdaterKernel {

    protected final FileCollector collector;
    protected final FileProcessor processor;
    protected final ResultStatistic statistic = ResultStatistic.getInstance();
    private final ExecutorService executor = Executors.newFixedThreadPool(InputConfig.getInstance().getThreads());

    protected ProgressBarBuilder pbBuilder() {
        return new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(250)
                .setMaxRenderedLength(75);
    }

    @SneakyThrows
    public void execute() {
        statistic.startTimer();

        try (ProgressBar progressBar = pbBuilder().build()) {
            List<File> files = loadFiles(InputConfig.getInstance().getLibraryPath().getAbsolutePath());
            progressBar.maxHint(files.size());

            files.forEach(file -> executor.submit(() -> {
                process(file);
                progressBar.step();
            }));

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        }

        endProcess();

        statistic.stopTimer();
        statistic.printResult();
    }

    protected List<File> loadExcludedFiles() {
        List<File> excludedFiles = InputConfig.getInstance().getExcludedDirectories().stream()
                .map(collector::loadFiles)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        statistic.increaseTotalBy(excludedFiles.size());
        statistic.increaseExcludedBy(excludedFiles.size());
        return excludedFiles;
    }

    /**
     * Load files or directories to update.
     * Remove excluded directories.
     *
     * @param path Path to library
     * @return List of files to update.
     */
    abstract List<File> loadFiles(String path);

    /**
     * Start of the file updating process.
     * This method is called by the executor and its contents are executed in parallel.
     *
     * @param file file or directory to update
     */
    void process(File file) {
        FileInfo fileInfo = new FileInfo(file);
        List<FileAttribute> attributes = processor.loadAttributes(file);

        if (attributes == null || attributes.isEmpty()) {
            log.warn("No attributes found for file {}", file);
            statistic.total();
            statistic.failure();
            return;
        }

        List<FileAttribute> nonForcedTracks = processor.retrieveNonForcedTracks(attributes);
        List<FileAttribute> nonCommentaryTracks = processor.retrieveNonCommentaryTracks(attributes);

        processor.detectDefaultTracks(fileInfo, attributes, nonForcedTracks);
        processor.detectDesiredTracks(fileInfo, nonForcedTracks, nonCommentaryTracks,
                InputConfig.getInstance().getAttributeConfig().toArray(new AttributeConfig[]{}));

        updateFile(fileInfo);
    }

    /**
     * Persist file changes.
     *
     * @param fileInfo contains information about file and desired configuration.
     */
    protected void updateFile(FileInfo fileInfo) {
        statistic.total();
        switch (fileInfo.getStatus()) {
            case CHANGE_NECESSARY:
                statistic.shouldChange();
                commitChange(fileInfo);
                break;
            case NO_SUITABLE_CONFIG:
                statistic.noSuitableConfigFound();
                break;
            case ALREADY_SUITED:
                statistic.alreadyFits();
                break;
            case UNKNOWN:
            default:
                statistic.failure();
                break;
        }
    }

    private void commitChange(FileInfo fileInfo) {
        if (InputConfig.getInstance().isSafeMode()) {
            return;
        }

        try {
            processor.update(fileInfo.getFile(), fileInfo);
            statistic.success();
            log.info("Commited {} to '{}'", fileInfo.getMatchedConfig().toStringShort(), fileInfo.getFile().getAbsolutePath());
        } catch (IOException | MkvToolNixException e) {
            statistic.failedChanging();
            log.warn("Couldn't commit {} to '{}'", fileInfo.getMatchedConfig().toStringShort(), fileInfo.getFile().getAbsoluteFile(), e);
        }
    }

    protected void endProcess() {
        if (InputConfig.getInstance().isSafeMode()) {
            return;
        }

        try {
            String filePath = AppDirsFactory.getInstance().getUserConfigDir(ProjectUtil.getProjectName(), null, null);

            File configDir = Path.of(filePath).toFile();
            if (!configDir.exists()) configDir.mkdirs();

            File lastExecutionFile = Path.of(filePath + "/last-execution.yml").toFile();
            if (!lastExecutionFile.exists()) lastExecutionFile.createNewFile();

            YAML yaml = new YAML(lastExecutionFile);
            yaml.set(InputConfig.getInstance().getNormalizedLibraryPath(), DateUtils.convert(new Date()));
            yaml.save(lastExecutionFile);
        } catch (IOException | YamlInvalidContentException e) {
            log.error("last-execution.yml could not be created or read.", e);
        }
    }
}

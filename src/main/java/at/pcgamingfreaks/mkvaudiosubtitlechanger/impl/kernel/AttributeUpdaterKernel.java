package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.AttributeProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AttributeUpdaterKernel {

    protected final InputConfig config;
    protected final FileProcessor processor;
    protected final AttributeProcessor attributeProcessor;
    protected final ResultStatistic statistic = ResultStatistic.getInstance();

    private final ExecutorService executor;

    public AttributeUpdaterKernel(InputConfig config, FileProcessor processor) {
        this.config = config;
        this.processor = processor;
        this.attributeProcessor = new AttributeProcessor(config.getPreferredSubtitles().toArray(new String[0]), config.getForcedKeywords(), config.getCommentaryKeywords(), config.getHearingImpaired());
        this.executor = Executors.newFixedThreadPool(config.getThreads());
    }

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
            List<File> files = processor.loadFiles(InputConfig.getInstance().getLibraryPath().getAbsolutePath());

            progressBar.maxHint(files.size());
            progressBar.refresh();

            files.forEach(file -> executor.submit(() -> {
                process(file);
                progressBar.step();
            }));

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        }

        writeLastExecutionDate();

        statistic.stopTimer();
        statistic.printResult();
    }

    /**
     * Start of the file updating process.
     * This method is called by the executor and its contents are executed in parallel.
     *
     * @param file file or directory to update
     */
    void process(File file) {
        FileInfo fileInfo = processor.readAttributes(file);

        if (fileInfo.getTracks().isEmpty()) {
            log.warn("No attributes found for file {}", file);
            statistic.failure();
            return;
        }

        attributeProcessor.findDefaultMatchAndApplyChanges(fileInfo);
        attributeProcessor.findForcedTracksAndApplyChanges(fileInfo, config.isOverwriteForced());
        attributeProcessor.findCommentaryTracksAndApplyChanges(fileInfo);
        attributeProcessor.findHearingImpairedTracksAndApplyChanges(fileInfo);

        checkStatusAndUpdate(fileInfo);
    }

    /**
     * Persist file changes.
     *
     * @param fileInfo contains information about file and desired configuration.
     */
    protected void checkStatusAndUpdate(FileInfo fileInfo) {
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
        if (InputConfig.getInstance().isSafeMode()) return;

        try {
            processor.update(fileInfo);
            statistic.success();
            log.info("Commited {} to '{}'", fileInfo.getMatchedConfig().toStringShort(), fileInfo.getFile().getAbsolutePath());
        } catch (IOException | MkvToolNixException e) {
            statistic.failedChanging();
            log.warn("Couldn't commit {} to '{}'", fileInfo.getMatchedConfig().toStringShort(), fileInfo.getFile().getAbsoluteFile(), e);
        }
    }

    // should this be here?
    protected void writeLastExecutionDate() {
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

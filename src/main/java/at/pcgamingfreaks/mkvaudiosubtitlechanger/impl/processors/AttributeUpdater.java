package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AttributeUpdater {

    protected final InputConfig config;
    protected final FileProcessor fileProcessor;
    protected final AttributeChangeProcessor attributeChangeProcessor;
    protected final ResultStatistic statistic = ResultStatistic.getInstance();

    private final ExecutorService executor;

    public AttributeUpdater(InputConfig config, FileProcessor fileProcessor) {
        this.config = config;
        this.fileProcessor = fileProcessor;
        this.attributeChangeProcessor = attributeChangeProcessor;
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
            List<File> files = getFiles();

            progressBar.maxHint(files.size());
            progressBar.refresh();

            files.forEach(file -> executor.submit(() -> {
                process(file);
                progressBar.step();
            }));

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        }

//        writeLastExecutionDate();

        statistic.stopTimer();
        statistic.print();
    }

    protected abstract List<File> getFiles();

    /**
     * Start of the file updating process.
     * This method is called by the executor and its contents are executed in parallel.
     *
     * @param file file or directory to update
     */
    protected abstract void process(File file);

    /**
     * Persist file changes.
     *
     * @param fileInfo contains information about file and desired configuration.
     */
    protected void checkStatusAndUpdate(FileInfo fileInfo) {
        if (!fileInfo.getChanges().isEmpty()) {
            statistic.changePlanned();

            if (config.isSafeMode()) return;

            try {
                fileProcessor.update(fileInfo);
                statistic.changeSuccessful();
                log.info("Commited {} to '{}'", fileInfo.getMatchedConfig().toStringShort(), fileInfo.getFile().getPath());
            } catch (IOException | MkvToolNixException e) {
                statistic.changeFailed();
                log.warn("Couldn't commit {} to '{}'", fileInfo.getMatchedConfig().toStringShort(), fileInfo.getFile().getPath(), e);
            }
        } else if (fileInfo.getChanges().isEmpty()) {
            statistic.unchanged();
        } else {
            statistic.unknownFailed();
        }
    }

    // should this be here?
//    protected void writeLastExecutionDate() {
//        if (config.isSafeMode()) {
//            return;
//        }
//
//        try {
//            String filePath = AppDirsFactory.getInstance().getUserConfigDir(ProjectUtil.getProjectName(), null, null);
//
//            File configDir = Path.of(filePath).toFile();
//            if (!configDir.exists()) configDir.mkdirs();
//
//            File lastExecutionFile = Path.of(filePath + "/last-execution.yml").toFile();
//            if (!lastExecutionFile.exists()) lastExecutionFile.createNewFile();
//
//            YAML yaml = new YAML(lastExecutionFile);
//            yaml.set(config.getNormalizedLibraryPath(), DateUtils.convert(new Date()));
//            yaml.save(lastExecutionFile);
//        } catch (IOException | YamlInvalidContentException e) {
//            log.error("last-execution.yml could not be created or read.", e);
//        }
//    }
}

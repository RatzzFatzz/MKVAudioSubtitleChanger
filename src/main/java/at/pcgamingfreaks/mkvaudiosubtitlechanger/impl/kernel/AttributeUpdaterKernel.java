package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public abstract class AttributeUpdaterKernel {

    protected final FileCollector collector;
    protected final FileProcessor processor;
    protected final ResultStatistic statistic = new ResultStatistic();
    private final ExecutorService executor = Executors.newFixedThreadPool(Config.getInstance().getThreads());

    private static ProgressBarBuilder pbBuilder() {
        return new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(250)
                .setMaxRenderedLength(75);
    }

    @SneakyThrows
    public void execute() {
        statistic.startTimer();

        try (ProgressBar progressBar = pbBuilder().build()) {
            List<File> files = loadFiles(Config.getInstance().getLibraryPath().getAbsolutePath());
            progressBar.maxHint(files.size());

            files.forEach(file -> executor.submit(() -> {
                process(file);
                progressBar.step();
            }));

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        }

        statistic.stopTimer();
        statistic.printResult();
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
    abstract void process(File file);

    /**
     * Persist file changes.
     *
     * @param fileInfoDto contains information about file and desired configuration.
     */
    protected void updateFile(FileInfoDto fileInfoDto) {
        statistic.total();
        switch (fileInfoDto.getStatus()) {
            case CHANGE_NECESSARY:
                statistic.shouldChange();
                commitChange(fileInfoDto);
                break;
            case UNABLE_TO_APPLY:
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

    private void commitChange(FileInfoDto fileInfo) {
        if (Config.getInstance().isSafeMode()) {
            return;
        }

        try {
            processor.update(fileInfo.getFile(), fileInfo);
            statistic.success();
            log.info("Updated {}", fileInfo.getFile().getAbsolutePath());
        } catch (IOException | MkvToolNixException e) {
            statistic.failedChanging();
            log.warn("File couldn't be updated: '{}', Error: {}", fileInfo.getFile().getAbsoluteFile(), e.getMessage());
        }
    }
}

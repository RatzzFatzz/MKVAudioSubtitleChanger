package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
public class AttributeUpdaterKernel {

    private final ExecutorService executor = Executors.newFixedThreadPool(Config.getInstance().getThreadCount());
    private final FileCollector collector;
    private final FileProcessor processor;
    private final ResultStatistic statistic = new ResultStatistic();

    public AttributeUpdaterKernel(FileCollector collector, FileProcessor processor) {
        this.collector = collector;
        this.processor = processor;
    }

    @SneakyThrows
    public void execute() {
        statistic.startTimer();

        try (ProgressBar progressBar = pbBuilder().build()) {
            List<File> excludedFiles = Config.getInstance().getExcludedDirectories().stream()
                    .map(collector::loadFiles)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            List<File> files = collector.loadFiles(Config.getInstance().getLibraryPath()).stream()
                    .filter(file -> !excludedFiles.contains(file))
                    .collect(Collectors.toList());
            progressBar.maxHint(files.size());
            files.forEach(file -> executor.submit(() -> process(file, progressBar)));
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        }

        statistic.stopTimer();
        System.out.println(statistic);
        log.info(statistic);
    }

    private void process(File file, ProgressBar progressBar) {
        statistic.total();
        List<FileAttribute> attributes = processor.loadAttributes(file);
        FileInfoDto fileInfo = processor.filterAttributes(attributes);
        if (fileInfo.isChangeNecessary()) {
            statistic.shouldChange();
            if (!Config.getInstance().isSafeMode()) {
                try {
                    processor.update(file, fileInfo);
                    statistic.success();
                } catch (IOException e) {
                    statistic.failedChanging();
                    log.warn("File couldn't be updated: {}", file.getAbsoluteFile());
                }
            }
        } else if (fileInfo.isUnableToApplyConfig()) {
            statistic.noSuitableConfigFound();
        } else if (fileInfo.isAlreadySuitable()){
            statistic.alreadyFits();
        } else {
            statistic.failure();
        }
        progressBar.step();
    }

    private static ProgressBarBuilder pbBuilder() {
        return new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(250)
                .setMaxRenderedLength(75);
    }
}

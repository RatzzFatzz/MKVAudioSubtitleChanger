package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        List<File> files = collector.loadFiles(Config.getInstance().getLibraryPath());
        files.forEach(file -> executor.submit(() -> process(file)));
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

        statistic.stopTimer();
        System.out.println(statistic);
    }

    private void process(File file) {
        List<FileAttribute> attributes = processor.loadAttributes(file);
        FileInfoDto fileInfo = processor.filterAttributes(attributes);
        if (fileInfo.isChangeNecessary()) {
            statistic.shouldChange(file, fileInfo);
            if (!Config.getInstance().isSafeMode()) {
                try {
                    processor.update(file, fileInfo);
                    statistic.success(file, fileInfo);
                } catch (IOException e) {
                    statistic.failure(file, fileInfo);
                    log.warn("File couldn't be updated: {}", file.getAbsoluteFile());
                }
            }
        } else {
            statistic.fits(file, fileInfo);
        }
    }
}

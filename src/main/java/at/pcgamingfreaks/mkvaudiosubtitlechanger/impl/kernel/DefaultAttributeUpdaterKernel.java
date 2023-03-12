package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DefaultAttributeUpdaterKernel extends AttributeUpdaterKernel {

    public DefaultAttributeUpdaterKernel(FileCollector collector, FileProcessor processor) {
        super(collector, processor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" files", 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<File> loadFiles(String path) {
        List<File> excludedFiles = loadExcludedFiles();
        return collector.loadFiles(Config.getInstance().getLibraryPath().getAbsolutePath()).stream()
                .filter(file -> !excludedFiles.contains(file))
                .collect(Collectors.toList());
    }
}

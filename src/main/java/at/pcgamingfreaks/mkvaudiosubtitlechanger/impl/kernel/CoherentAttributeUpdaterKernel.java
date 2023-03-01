package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CoherentAttributeUpdaterKernel extends AttributeUpdaterKernel {

    public CoherentAttributeUpdaterKernel(FileCollector collector, FileProcessor processor) {
        super(collector, processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<File> loadFiles(String path) {
        List<File> excludedFiles = loadExcludedFiles();
        List<File> directories = collector.loadDirectories(path, Config.getInstance().getCoherent())
                .stream().filter(file -> !excludedFiles.contains(file))
                .collect(Collectors.toList());
        return directories.stream()
                .filter(dir -> isParentDirectory(dir, directories))
                .collect(Collectors.toList());
    }

    private boolean isParentDirectory(File directory, List<File> directories) {
        String path = directory.getAbsolutePath();
        return directories.stream()
                .noneMatch(dir -> dir.getAbsolutePath().contains(path) && !StringUtils.equals(path, dir.getAbsolutePath()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void process(File file) {

    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.Cache;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CachedFileProcessor implements FileProcessor {
    private final FileProcessor processor;
    Cache<String, List<File>> fileCache = new Cache<>();
    Cache<Pair<String, Integer>, List<File>> directoryCache = new Cache<>();
    Cache<File, FileInfo> attributeCache = new Cache<>();

    public CachedFileProcessor(FileProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<File> loadFiles(String path) {
        return fileCache.retrieve(path, processor::loadFiles);
    }

    @Override
    public List<File> loadDirectories(String path, int depth) {
        return directoryCache.retrieve(Pair.of(path, depth), key -> processor.loadDirectories(key.getLeft(), key.getRight()));
    }

    @Override
    public FileInfo readAttributes(File file) {
        return attributeCache.retrieve(file, processor::readAttributes);
    }

    @Override
    public void update(FileInfo fileInfo) throws IOException, MkvToolNixException {
        processor.update(fileInfo);
    }

}

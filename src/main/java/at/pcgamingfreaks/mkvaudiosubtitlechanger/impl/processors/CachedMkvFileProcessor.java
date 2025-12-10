package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.Cache;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;

import java.io.File;

public class CachedMkvFileProcessor extends MkvFileProcessor {
    Cache<File, FileInfo> cache = new Cache<>();

    @Override
    public FileInfo readAttributes(File file) {
        return cache.retrieve(file, super::readAttributes);
    }
}

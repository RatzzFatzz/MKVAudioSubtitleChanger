package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;

import java.io.File;
import java.util.List;

public class CachedMkvFileProcessor extends MkvFileProcessor {
    Cache<File, List<FileAttribute>> cache = new Cache<>();

    @Override
    public List<FileAttribute> loadAttributes(File file) {
        return cache.retrieve(file, super::loadAttributes);
    }
}

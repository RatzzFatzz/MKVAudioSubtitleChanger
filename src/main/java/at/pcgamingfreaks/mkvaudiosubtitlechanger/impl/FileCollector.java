package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import java.io.File;
import java.util.List;

public interface FileCollector {

    /**
     * @param path leads to one file directly or a directory which will be loaded recursively
     * @return list of all files within the directory
     */
    List<File> loadFiles(String path);
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import java.io.File;
import java.util.List;

public interface FileCollector {

    /**
     * @param path leads to one file directly or a directory which will be loaded recursively
     * @return list of all files within the directory
     */
    List<File> loadFiles(String path);

    /**
     * Load all directories from path, but only until depth is reached.
     *
     * @param path leads to a directory which will be loaded recursively until depth
     * @param depth limit directory crawling
     * @return list of directory until depth
     */
    List<File> loadDirectories(String path, int depth);
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileProcessor {

    /**
     * @param path leads to one file directly or a directory which will be loaded recursively
     * @return list of all files within the directory
     */
    List<File> loadFiles(String path);

    /**
     * Load all directories from path, but only until depth is reached.
     * This will ignore all files between root and directories at depth.
     *
     * @param path leads to a directory which will be loaded recursively until depth
     * @param depth limit directory crawling
     * @return list of directory until depth
     */
    List<File> loadDirectories(String path, int depth);

    /**
     * Load track information from file.
     *
     * @param file Takes the file from which the attributes will be returned
     * @return list of all important attributes
     */
    FileInfo readAttributes(File file);

    /**
     * Update the file.
     *
     * @param fileInfo information used to update file
     * @throws IOException         when error occurs accessing file retrieving information
     * @throws MkvToolNixException when error occurs while sending query to mkvpropedit
     */
    void update(FileInfo fileInfo) throws IOException, MkvToolNixException;
}

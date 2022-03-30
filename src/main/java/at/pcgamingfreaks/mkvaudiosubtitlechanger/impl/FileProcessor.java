package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileProcessor {

    /**
     * @param file Takes the file from which the attributes will be returned
     * @return list of all important attributes
     */
    List<FileAttribute> loadAttributes(File file);

    FileInfoDto filterAttributes(List<FileAttribute> attributes);

    void update(File file, FileInfoDto fileInfo) throws IOException;
}

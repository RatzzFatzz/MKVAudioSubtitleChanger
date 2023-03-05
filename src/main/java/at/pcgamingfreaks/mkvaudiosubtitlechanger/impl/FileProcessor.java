package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileProcessor {

    /**
     * Load track information from file.
     *
     * @param file Takes the file from which the attributes will be returned
     * @return list of all important attributes
     */
    List<FileAttribute> loadAttributes(File file);

    /**
     * Populate FileInfoDto with the currently set default tracks.
     * @param info to be populated
     * @param attributes Track information of FileInfoDto
     * @param nonForcedTracks List of all not forced tracks
     */
    void detectDefaultTracks(FileInfoDto info, List<FileAttribute> attributes, List<FileAttribute> nonForcedTracks);

    /**
     * Populate FileInfoDto with the desired tracks, based on AttributeConfig.
     * @param info to be populated
     * @param nonForcedTracks List of all not forced tracks
     * @param nonCommentaryTracks  List of all not commentary tracks
     * @param configs
     */
    void detectDesiredTracks(FileInfoDto info, List<FileAttribute> nonForcedTracks, List<FileAttribute> nonCommentaryTracks,
                             AttributeConfig... configs);

    List<FileAttribute> retrieveNonForcedTracks(List<FileAttribute> attributes);

    List<FileAttribute> retrieveNonCommentaryTracks(List<FileAttribute> attributes);

    /**
     * Update the file.
     * @param file to be updated
     * @param fileInfo information to update file
     * @throws IOException
     * @throws MkvToolNixException when error occurs while sending query to mkvpropedit
     */
    void update(File file, FileInfoDto fileInfo) throws IOException, MkvToolNixException;
}

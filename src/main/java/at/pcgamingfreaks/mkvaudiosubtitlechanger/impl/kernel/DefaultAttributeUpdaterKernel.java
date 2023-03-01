package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DefaultAttributeUpdaterKernel extends AttributeUpdaterKernel {

    public DefaultAttributeUpdaterKernel(FileCollector collector, FileProcessor processor) {
        super(collector, processor);
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

    /**
     * {@inheritDoc}
     */
    @Override
    void process(File file) {
        FileInfoDto fileInfo = new FileInfoDto(file);
        List<FileAttribute> attributes = processor.loadAttributes(file);

        // TODO: extract this to a attributeProcessor?
        List<FileAttribute> nonForcedTracks = attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.getTrackName(),
                        Config.getInstance().getForcedKeywords().toArray(new CharSequence[0])))
                .filter(elem -> !elem.isForcedTrack())
                .collect(Collectors.toList());
        List<FileAttribute> nonCommentaryTracks = attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.getTrackName(),
                        Config.getInstance().getCommentaryKeywords().toArray(new CharSequence[0])))
                .collect(Collectors.toList());

        processor.detectDefaultTracks(fileInfo, attributes, nonForcedTracks);
        processor.detectDesiredTracks(fileInfo, nonForcedTracks, nonCommentaryTracks);

        updateFile(fileInfo);
    }
}

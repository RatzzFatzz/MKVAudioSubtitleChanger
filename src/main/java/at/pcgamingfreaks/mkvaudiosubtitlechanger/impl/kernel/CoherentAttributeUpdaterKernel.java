package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<FileInfoDto> fileInfos = collector.loadFiles(file.getAbsolutePath())
                .stream().map(FileInfoDto::new)
                .collect(Collectors.toList());

        Map<FileInfoDto, List<FileAttribute>> fileAttributeCache = new HashMap<>();
        for (FileInfoDto fileInfo : fileInfos) {
            if (!Config.getInstance().getIncludePattern().matcher(fileInfo.getFile().getAbsolutePath()).matches()) {
                statistic.excluded();
                continue;
            }
            fileAttributeCache.put(fileInfo, processor.loadAttributes(fileInfo.getFile()));
        }

        for (AttributeConfig config : Config.getInstance().getAttributeConfig()) {
            for (FileInfoDto fileInfo : fileInfos) {
                List<FileAttribute> attributes = fileAttributeCache.get(fileInfo);

                List<FileAttribute> nonForcedTracks = processor.retrieveNonForcedTracks(attributes);
                List<FileAttribute> nonCommentaryTracks = processor.retrieveNonCommentaryTracks(attributes);

                processor.detectDefaultTracks(fileInfo, attributes, nonForcedTracks);
                processor.detectDesiredTracks(fileInfo, nonForcedTracks, nonCommentaryTracks, config);
            }

            if (fileInfos.stream().allMatch(elem -> elem.getDesiredSubtitleLane() != null && elem.getDesiredAudioLane() != null)) {
                log.debug("Found {}/{} match for {}", config.getAudioLanguage(), config.getSubtitleLanguage(), file.getAbsolutePath());
                break;
            }

            fileInfos.forEach(f -> {
                f.setDesiredAudioLane(null);
                f.setDesiredSubtitleLane(null);
            });
        }

        // apply config

        // apply default process if nothing was found (if parameter is set)
    }
}

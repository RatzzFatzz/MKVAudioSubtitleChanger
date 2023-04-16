package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;
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

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" directories", 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<File> loadFiles(String path) {
        return loadFiles(path, Config.getInstance().getCoherent());
    }

    List<File> loadFiles(String path, int depth) {
        List<File> excludedFiles = loadExcludedFiles();
        List<File> directories = collector.loadDirectories(path, depth)
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
     * Update files in directory, if possible, with the same {@link AttributeConfig}.
     * If {@link Config#isForceCoherent()} then there will be no changes to the file if they don't match the same config.
     * Otherwise, the default behaviour is executed.
     * This method is called by the executor and is run in parallel.
     *
     * @param file directory containing files
     */
    @Override
    void process(File file) {
        process(file, Config.getInstance().getCoherent());
    }

    void process(File file, int depth) {
        // TODO: Implement level crawl if coherence is not possible on user entered depth
        // IMPL idea: recursive method call, cache needs to be implemented
        List<FileInfoDto> fileInfos = collector.loadFiles(file.getAbsolutePath()).stream()
                .map(FileInfoDto::new)
                .collect(Collectors.toList());

        for (AttributeConfig config : Config.getInstance().getAttributeConfig()) {

            for (FileInfoDto fileInfo : fileInfos) {
                List<FileAttribute> attributes = processor.loadAttributes(fileInfo.getFile());

                List<FileAttribute> nonForcedTracks = processor.retrieveNonForcedTracks(attributes);
                List<FileAttribute> nonCommentaryTracks = processor.retrieveNonCommentaryTracks(attributes);

                processor.detectDefaultTracks(fileInfo, attributes, nonForcedTracks);
                processor.detectDesiredTracks(fileInfo, nonForcedTracks, nonCommentaryTracks, config);
            }

            if (fileInfos.stream().allMatch(elem -> elem.getDesiredSubtitleLane() != null && elem.getDesiredAudioLane() != null)) {
                log.info("Found {}/{} match for {}", config.getAudioLanguage(), config.getSubtitleLanguage(), file.getAbsolutePath());
                fileInfos.forEach(this::updateFile);
                return; // match found, end process here
            }

            fileInfos.forEach(f -> {
                f.setDesiredAudioLane(null);
                f.setDesiredSubtitleLane(null);
            });
        }

        log.info("No coherent match found for {}", file.getAbsoluteFile());

        for (FileInfoDto fileInfo : fileInfos) {
            statistic.total();
            if (!Config.getInstance().isForceCoherent()) {
                super.process(fileInfo.getFile());
            } else {
                statistic.excluded();
            }
        }
    }
}

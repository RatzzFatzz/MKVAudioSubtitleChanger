package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CoherentAttributeUpdaterKernel extends AttributeUpdaterKernel {

    public CoherentAttributeUpdaterKernel(InputConfig config, FileProcessor processor) {
        super(config, processor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" directories", 1);
    }

    List<File> loadFiles(String path, int depth) {
        List<File> directories = processor.loadDirectories(path, depth)
                .stream()
//                .filter(file -> !excludedFiles.contains(file))
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
     * If {@link InputConfig#isForceCoherent()} then there will be no changes to the file if they don't match the same config.
     * Otherwise, the default behaviour is executed.
     * This method is called by the executor and is run in parallel.
     *
     * @param file directory containing files
     */
    @Override
    void process(File file) {
        process(file, InputConfig.getInstance().getCoherent());
    }

    void process(File file, int depth) {
        // TODO: Implement level crawl if coherence is not possible on user entered depth
        // IMPL idea: recursive method call, cache needs to be implemented
//        List<FileInfoOld> fileInfoOlds = collector.loadFiles(file.getAbsolutePath()).stream()
//                .map(FileInfoOld::new)
//                .collect(Collectors.toList());

        for (AttributeConfig config : InputConfig.getInstance().getAttributeConfig()) {

//            for (FileInfoOld fileInfoOld : fileInfoOlds) {
//                List<TrackAttributes> attributes = processor.readAttributes(fileInfoOld.getFile());
//
//                List<TrackAttributes> nonForcedTracks = processor.retrieveNonForcedTracks(attributes);
//                List<TrackAttributes> nonCommentaryTracks = processor.retrieveNonCommentaryTracks(attributes);
//
//                processor.detectDefaultTracks(fileInfoOld, attributes, nonForcedTracks);
//                processor.detectDesiredTracks(fileInfoOld, nonForcedTracks, nonCommentaryTracks, config);
//            }
//
//            if (fileInfoOlds.stream().allMatch(elem -> ("OFF".equals(config.getSubtitleLanguage()) || elem.getDesiredDefaultSubtitleLane() != null)
//                    && elem.getDesiredDefaultAudioLane() != null)) {
//                log.info("Found {} match for {}", config.toStringShort(), file.getAbsolutePath());
//                fileInfoOlds.forEach(this::updateFile);
//                return; // match found, end process here
//            }

//            fileInfoOlds.forEach(f -> {
//                f.setDesiredDefaultAudioLane(null);
//                f.setDesiredDefaultSubtitleLane(null);
//            });
        }

        log.info("No coherent match found for {}", file.getAbsoluteFile());

//        for (FileInfoOld fileInfoOld : fileInfoOlds) {
//            if (!InputConfig.getInstance().isForceCoherent()) {
//                super.process(fileInfoOld.getFile());
//            } else {
//                statistic.excluded();
//            }
//        }
    }
}

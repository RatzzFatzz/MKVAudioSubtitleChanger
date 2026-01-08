package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.LastExecutionHandler;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CoherentAttributeUpdater extends SingleFileAttributeUpdater {

    public CoherentAttributeUpdater(InputConfig config, FileProcessor processor, AttributeChangeProcessor attributeChangeProcessor, LastExecutionHandler lastExecutionHandler) {
        super(config, processor, attributeChangeProcessor, lastExecutionHandler);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" directories", 1);
    }

    protected List<File> getFiles() {
        return Arrays.stream(config.getLibraryPath())
                .flatMap(path -> fileProcessor.loadDirectory(path.getPath(), config.getCoherent()).stream())
                .toList();
    }

    @Override
    public void process(File rootDir) {
        if (rootDir.isFile()) {
            super.process(rootDir);
            return;
        }

        List<File> files = fileProcessor.loadFiles(rootDir.getPath());
        Set<FileInfo> matchedFiles = new HashSet<>(files.size() * 2);

        for (AttributeConfig config: config.getAttributeConfig()) {
            AttributeConfig matchedConfig = findMatch(config, matchedFiles, files);

            if (matchedConfig == null) continue;
            if (matchedFiles.size() != files.size()) {
                log.warn("Skip applying changes: Found coherent match, but matched count is different than file count (matched: {}, files: {}, dir: {})",
                        matchedFiles.size(), files.size(), rootDir.getPath());
            }

            log.info("Found coherent match {} for {}", matchedConfig.toStringShort(), rootDir.getPath());
            matchedFiles.forEach(fileInfo -> {
                attributeChangeProcessor.findAndApplyForcedTracks(fileInfo, this.config.isOverwriteForced());
                attributeChangeProcessor.applyForcedAsDefault(fileInfo);
                attributeChangeProcessor.findAndApplyCommentaryTracks(fileInfo);
                attributeChangeProcessor.findAndApplyHearingImpairedTracks(fileInfo);

                checkStatusAndUpdate(fileInfo);
            });
            return; // match was found and process must be stopped
        }

        // Couldn't match any config at current level. Resetting changes and trying one level deeper
        matchedFiles.forEach(fileInfo -> {
            fileInfo.resetChanges();
            fileInfo.setMatchedConfig(null);
        });

        if (config.isForceCoherent()) {
            log.info("No coherent match found, skipping {}", rootDir.getPath());
            statistic.increaseUnchangedBy(files.size());
            return;
        }

        log.info("No coherent match found, attempting to find coherent match in child directories of {}", rootDir.getPath());
        for (File dir: fileProcessor.loadDirectory(rootDir.getPath(), 1)) this.process(dir);
    }

    private AttributeConfig findMatch(AttributeConfig config, Set<FileInfo> matchedFiles, List<File> files) {
        AttributeConfig matchedConfig = null;
        matchedFiles.clear();

        for (File file: files)  {
            FileInfo fileInfo = fileProcessor.readAttributes(file);
            fileInfo.resetChanges();
            fileInfo.setMatchedConfig(null);

            if (fileInfo.getTracks().isEmpty()) {
                log.warn("No attributes found for {}", file);
                statistic.unknownFailed();
                break;
            }

            attributeChangeProcessor.findAndApplyDefaultMatch(fileInfo, config);

            if (matchedConfig == null) matchedConfig = fileInfo.getMatchedConfig();
            if (matchedConfig == null || matchedConfig != fileInfo.getMatchedConfig()) {
                matchedConfig = null;
                break;
            }
            matchedFiles.add(fileInfo);
        }

        return matchedConfig;
    }
}

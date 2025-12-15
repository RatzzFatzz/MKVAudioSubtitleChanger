package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CoherentAttributeUpdater extends SingleFileAttributeUpdater {

    public CoherentAttributeUpdater(InputConfig config, FileProcessor processor) {
        super(config, processor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" directories", 1);
    }

    @Override
    public void process(File rootDir) {
        if (rootDir.isFile()) {
            super.process(rootDir);
            return;
        }

        List<File> files = fileProcessor.loadFiles(rootDir.getPath());
        Set<FileInfo> matchedFiles = new HashSet<>(files.size() * 2);

        AttributeConfig matchedConfig = null;
        for (AttributeConfig config: config.getAttributeConfig()) {
            for (File file: files)  {
                FileInfo fileInfo = fileProcessor.readAttributes(file);
                fileInfo.resetChanges();
                fileInfo.setMatchedConfig(null);

                if (fileInfo.getTracks().isEmpty()) {
                    log.warn("No attributes found for file {}", file);
                    statistic.failure();
                    break;
                }

                attributeProcessor.findDefaultMatchAndApplyChanges(fileInfo, config);

                if (matchedConfig == null) matchedConfig = fileInfo.getMatchedConfig();
                matchedFiles.add(fileInfo);
                if (matchedConfig != fileInfo.getMatchedConfig()) {
                    matchedConfig = null;
                    break;
                }
            }

            if (matchedConfig != null) break;
        }

        if (matchedConfig != null) {
            matchedFiles.forEach(fileInfo -> {
                attributeProcessor.findForcedTracksAndApplyChanges(fileInfo, config.isOverwriteForced());
                attributeProcessor.findCommentaryTracksAndApplyChanges(fileInfo);
                attributeProcessor.findHearingImpairedTracksAndApplyChanges(fileInfo);

                checkStatusAndUpdate(fileInfo);
            });
        } else {
            log.info("No coherent match found, trying to find coherent match in child directories: {}", rootDir.getPath());
            matchedFiles.forEach(fileInfo -> {
                fileInfo.resetChanges();
                fileInfo.setMatchedConfig(null);
            });
            for (File dir: fileProcessor.loadDirectory(rootDir.getPath(), 1)) this.process(dir);
        }
    }
}

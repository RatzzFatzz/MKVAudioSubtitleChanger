package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SingleFileAttributeUpdater extends AttributeUpdater {

    public SingleFileAttributeUpdater(InputConfig config, FileProcessor processor, AttributeChangeProcessor attributeChangeProcessor) {
        super(config, processor, attributeChangeProcessor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" files", 1);
    }

    @Override
    protected List<File> getFiles() {
        return Arrays.stream(config.getLibraryPaths())
                .flatMap(path -> fileProcessor.loadFiles(path.getPath()).stream())
                .toList();
    }

    @Override
    public void process(File file) {
        FileInfo fileInfo = fileProcessor.readAttributes(file);

        if (fileInfo.getTracks().isEmpty()) {
            log.warn("No attributes found for {}", file);
            statistic.unknownFailed();
            return;
        }

        attributeChangeProcessor.findAndApplyDefaultMatch(fileInfo, config.getAttributeConfig());
        attributeChangeProcessor.findAndApplyForcedTracks(fileInfo, config.isOverwriteForced());
        attributeChangeProcessor.applyForcedAsDefault(fileInfo);
        attributeChangeProcessor.findAndApplyCommentaryTracks(fileInfo);
        attributeChangeProcessor.findAndApplyHearingImpairedTracks(fileInfo);

        checkStatusAndUpdate(fileInfo);
    }
}

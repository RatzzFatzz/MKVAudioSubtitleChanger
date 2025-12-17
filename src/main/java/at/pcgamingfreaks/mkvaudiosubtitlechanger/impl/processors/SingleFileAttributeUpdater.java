package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import java.util.List;

@Slf4j
public class SingleFileAttributeUpdater extends AttributeUpdater {

    public SingleFileAttributeUpdater(InputConfig config, FileProcessor processor) {
        super(config, processor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" files", 1);
    }

    @Override
    protected List<File> getFiles() {
        return fileProcessor.loadFiles(config.getLibraryPath().getPath());
    }

    @Override
    public void process(File file) {
        FileInfo fileInfo = fileProcessor.readAttributes(file);

        if (fileInfo.getTracks().isEmpty()) {
            log.warn("No attributes found for file {}", file);
            statistic.unknownFailed();
            return;
        }

        attributeChangeProcessor.findDefaultMatchAndApplyChanges(fileInfo, config.getAttributeConfig());
        attributeChangeProcessor.findForcedTracksAndApplyChanges(fileInfo, config.isOverwriteForced());
        attributeChangeProcessor.findCommentaryTracksAndApplyChanges(fileInfo);
        attributeChangeProcessor.findHearingImpairedTracksAndApplyChanges(fileInfo);

        checkStatusAndUpdate(fileInfo);
    }
}

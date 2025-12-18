package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.*;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "mkvaudiosubtitlechanger",
        usageHelpAutoWidth = true,
        customSynopsis = {
                "mkvaudiosubtitlechanger [-a <attributeConfig> [...<attributeConfig>]] [-s] <libraryPath>",
                "Example: mkvaudiosubtitlechanger -a eng:eng eng:ger -s /mnt/media/",
                ""
        },
        requiredOptionMarker = '*',
        sortOptions = false,
        mixinStandardHelpOptions = true,
        versionProvider = ProjectUtil.class
)
public class CommandRunner implements Runnable {
    @Getter
    @CommandLine.ArgGroup(exclusive = false)
    private InputConfig config;

    @Override
    public void run() {
        if (config.isDebug()) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        FileFilter fileFilter = new FileFilter(config.getExcluded(), config.getIncludePattern(), config.getFilterDate());
        FileProcessor fileProcessor = new CachedFileProcessor(new MkvFileProcessor(config.getMkvToolNix(), fileFilter));
        AttributeChangeProcessor attributeChangeProcessor = new AttributeChangeProcessor(config.getPreferredSubtitles().toArray(new String[0]), config.getForcedKeywords(), config.getCommentaryKeywords(), config.getHearingImpaired());

        AttributeUpdater kernel = config.getCoherent() != null
                ? new CoherentAttributeUpdater(config, fileProcessor, attributeChangeProcessor)
                : new SingleFileAttributeUpdater(config, fileProcessor, attributeChangeProcessor);
        kernel.execute();
    }
}

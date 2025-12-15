package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.AttributeUpdater;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.CoherentAttributeUpdater;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.SingleFileAttributeUpdater;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.CachedFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.MkvFileProcessor;
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
                "mkvaudiosubtitlechanger -a <attributeConfig> [...<attributeConfig>] -l <libraryPath> [-s]",
                "Example: mkvaudiosubtitlechanger -a eng:eng eng:ger -l /mnt/media/ -s",
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

        AttributeUpdater kernel = config.getCoherent() != null
                ? new CoherentAttributeUpdater(config, fileProcessor)
                : new SingleFileAttributeUpdater(config, fileProcessor);
        kernel.execute();
    }
}

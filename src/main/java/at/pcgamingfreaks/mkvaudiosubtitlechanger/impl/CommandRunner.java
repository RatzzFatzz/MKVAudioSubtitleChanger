package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.*;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
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

        if (config.isSafeMode()) {
            log.info("Safemode active. No files will be changed!");
            System.out.println("Safemode active. No files will be changed!");
        }

        String userLocal = getLogDirectory();
        if (userLocal == null) {
            log.error("Could not load log4j2 log info");
            System.out.println("Could not load log4j2 log info");
            System.exit(1);
        }

        LastExecutionHandler lastExecutionHandler = config.isOnlyNewFiles() ? new LastExecutionHandler(userLocal) : null;
        FileFilter fileFilter = new FileFilter(config.getExcluded(), config.getIncludePattern(), config.getFilterDate(), lastExecutionHandler);
        FileProcessor fileProcessor = new CachedFileProcessor(new MkvFileProcessor(config.getMkvToolNix(), fileFilter));
        AttributeChangeProcessor attributeChangeProcessor = new AttributeChangeProcessor(config.getPreferredSubtitles().toArray(new String[0]), config.getForcedKeywords(), config.getCommentaryKeywords(), config.getHearingImpaired());

        AttributeUpdater kernel = config.getCoherent() != null
                ? new CoherentAttributeUpdater(config, fileProcessor, attributeChangeProcessor, lastExecutionHandler)
                : new SingleFileAttributeUpdater(config, fileProcessor, attributeChangeProcessor, lastExecutionHandler);
        kernel.execute();
    }

    public String getLogDirectory() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        for (org.apache.logging.log4j.core.Appender appender : config.getAppenders().values()) {
            if (appender instanceof RollingFileAppender rollingFileAppender) {
                String fileName = rollingFileAppender.getFileName();
                return new java.io.File(fileName).getParentFile().getParent();
            }
        }

        log.warn("No file appender found in configuration");
        return null;
    }
}

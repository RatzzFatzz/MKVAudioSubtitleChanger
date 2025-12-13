package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileFilter;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.FileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.MkvFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation.ValidationExecutionStrategy;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.CachedFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.AttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.CoherentAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.DefaultAttributeUpdaterKernel;
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
public class Main implements Runnable {

    @Getter
    @CommandLine.ArgGroup(exclusive = false)
    private InputConfig config;

    public static void main(String[] args) {
        if (args.length == 0) {
            CommandLine.usage(Main.class, System.out);
            return;
        }

        new CommandLine(Main.class)
                .setExecutionStrategy(new ValidationExecutionStrategy())
                .execute(args);
    }

    @Override
    public void run() {
        if (config.isDebug()) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        FileFilter fileFilter = new FileFilter(config.getExcluded(), config.getIncludePattern(), config.getFilterDate());
        FileProcessor fileProcessor = new CachedFileProcessor(new MkvFileProcessor(config.getMkvToolNix(), fileFilter));

        AttributeUpdaterKernel kernel = config.getCoherent() != null
                ? new CoherentAttributeUpdaterKernel(config, fileProcessor)
                : new DefaultAttributeUpdaterKernel(config, fileProcessor);
        kernel.execute();
    }
}

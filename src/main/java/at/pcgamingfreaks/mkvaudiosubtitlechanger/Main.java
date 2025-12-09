package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation.ValidationExecutionStrategy;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CachedMkvFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.AttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.CoherentAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.DefaultAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
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

        InputConfig.setInstance(config);
        AttributeUpdaterKernel kernel = InputConfig.getInstance().getCoherent() != null
                ? new CoherentAttributeUpdaterKernel(new MkvFileCollector(), new CachedMkvFileProcessor())
                : new DefaultAttributeUpdaterKernel(new MkvFileCollector(), new CachedMkvFileProcessor());
        kernel.execute();
    }
}

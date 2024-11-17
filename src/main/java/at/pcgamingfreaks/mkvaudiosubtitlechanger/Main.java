package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CachedMkvFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.AttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.CoherentAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.DefaultAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(mixinStandardHelpOptions = true, versionProvider = ProjectUtil.class)
public class Main implements Runnable {

    @Getter
    @CommandLine.ArgGroup(exclusive = false)
    private Config config = Config.getInstance();

    @Override
    public void run() {
        Config.setInstance(config);
        AttributeUpdaterKernel kernel = Config.getInstance().getCoherent() != null
                ? new CoherentAttributeUpdaterKernel(new MkvFileCollector(), new CachedMkvFileProcessor())
                : new DefaultAttributeUpdaterKernel(new MkvFileCollector(), new CachedMkvFileProcessor());
        kernel.execute();
    }

    public static void main(String[] args) {
        new CommandLine(Main.class).execute(args);
    }
}

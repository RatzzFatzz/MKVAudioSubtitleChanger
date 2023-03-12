package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ConfigLoader;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.AttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.CoherentAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.DefaultAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ConfigLoader.initConfig(args);
        AttributeUpdaterKernel kernel = Config.getInstance().getCoherent() != null
                ? new CoherentAttributeUpdaterKernel(new MkvFileCollector(), new MkvFileProcessor())
                : new DefaultAttributeUpdaterKernel(new MkvFileCollector(), new MkvFileProcessor());
        kernel.execute();
    }
}

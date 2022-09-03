package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ConfigLoader;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileProcessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {
        ConfigLoader.initConfig(args);
//        Config.getInstance().initConfig(args);
//        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel(new MkvFileCollector(), new MkvFileProcessor());
//        kernel.execute();
    }
}

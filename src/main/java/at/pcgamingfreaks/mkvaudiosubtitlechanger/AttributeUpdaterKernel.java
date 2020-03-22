package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ConfigUtil;

import java.util.List;

public class AttributeUpdaterKernel {
    public void execute() {
        List<AttributeConfig> list = ConfigUtil.loadConfig();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ConfigProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ConfigUtil;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class AttributeUpdaterKernel {
    MkvFileCollector collector = new MkvFileCollector();

    public void execute(String path) {
        List<AttributeConfig> configPattern = ConfigUtil.loadConfig();
        List<File> allValidPaths = collector.loadFiles(path);
        if(! allValidPaths.isEmpty() && configPattern != null){
            for(File file : allValidPaths){
                List<FileAttribute> attributes = collector.loadAttributes(file);
                for(AttributeConfig config : configPattern){
                    /*
                     * Creating new ArrayList, because the method removes elements from the list by reference
                     */
                    boolean fileHasChanged = new ConfigProcessor(config).processConfig(file, new ArrayList<>(attributes));
                    if(fileHasChanged){
                        break;
                    }
                }
            }
        }else{
            log.error("Path is not valid or config has errors!");
        }
    }
}

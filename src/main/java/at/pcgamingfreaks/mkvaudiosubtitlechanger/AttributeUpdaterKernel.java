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
import java.util.Scanner;

@Log4j2
public class AttributeUpdaterKernel {
    MkvFileCollector collector = new MkvFileCollector();

    public void execute() {
        List<AttributeConfig> configPattern = ConfigUtil.loadConfig();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the path to the files which should be updated: ");
        List<File> allValidPaths = null;
        do{
            allValidPaths = collector.loadFiles(scanner.nextLine());
            if(allValidPaths == null){
                System.out.println("Please enter a valid path: ");
            }
        }while(allValidPaths == null);
        log.info(allValidPaths.size() + " files where found and will now be processed!");

        for(File file : allValidPaths){
            List<FileAttribute> attributes = collector.loadAttributes(file);
            for(AttributeConfig config : configPattern){
                /*
                 * Creating new ArrayList, because the method removes elements from the list by reference
                 */
                boolean fileHasChanged = new ConfigProcessor(config).processConfig(file, new ArrayList(allValidPaths));
                if(fileHasChanged){
                    break;
                }
            }
        }
    }
}

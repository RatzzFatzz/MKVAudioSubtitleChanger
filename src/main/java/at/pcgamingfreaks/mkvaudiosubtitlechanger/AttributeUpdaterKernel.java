package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ConfigUtil;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class AttributeUpdaterKernel {
    MkvFileCollector collector = new MkvFileCollector();
    public void execute() {
        List<AttributeConfig> configPattern = ConfigUtil.loadConfig();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the path to the files which should be updated: ");
        List<File> allValidPaths = collector.loadFiles(scanner.nextLine());

        for(File file: allValidPaths) {
            List<FileAttribute> attributes = collector.loadAttributes(file);
            for(AttributeConfig config: configPattern) {
                boolean fileIsChanged = config.processConfig(file, attributes);
                if(fileIsChanged) {
                    break;
                }
            }
        }
    }
}

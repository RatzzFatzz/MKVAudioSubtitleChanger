package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ConfigProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ConfigUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

@Log4j2
public class AttributeUpdaterKernel {
    MkvFileCollector collector = new MkvFileCollector();
    int filesChangedAmount = 0;
    int filesNotChangedAmount = 0;
    long runtime = 0;

    @SneakyThrows
    public void execute(String path) {
        List<AttributeConfig> configPattern = ConfigUtil.getInstance().getAttributeConfig();
        List<File> allValidPaths = collector.loadFiles(path);
        ExecutorService executor = Executors.newFixedThreadPool(ConfigUtil.getInstance().getThreadCount());

        long beforeTimer = System.currentTimeMillis();
        if(allValidPaths != null && configPattern != null){
            System.out.print("Running");
            allValidPaths.forEach(file -> executor.submit(() -> process(configPattern, file)));
        }else{
            log.error("Path is not valid or config has errors!");
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        runtime = System.currentTimeMillis() - beforeTimer;

        System.out.printf("%nFiles changed: %s%n", filesChangedAmount);
        System.out.printf("Files not changed: %s%n", filesNotChangedAmount);
        System.out.printf("Runtime: %ss%n", runtime / 1000);
    }

    private void process(List<AttributeConfig> configPattern, File file) {
        List<FileAttribute> attributes = collector.loadAttributes(file);
        boolean fileHasChanged = false;

        if (attributes.isEmpty()) return;
        for(AttributeConfig config : configPattern){
            fileHasChanged = new ConfigProcessor(config).processConfig(file, attributes);
            if(fileHasChanged) break;
        }
        if(!fileHasChanged){
            log.info("File didn't change: {}", file.getName());
            filesNotChangedAmount++;
        } else {
            filesChangedAmount++;
        }
        System.out.print(".");
    }
}

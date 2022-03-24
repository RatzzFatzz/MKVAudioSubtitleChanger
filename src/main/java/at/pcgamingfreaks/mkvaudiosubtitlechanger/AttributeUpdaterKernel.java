package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl.ConfigProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

@Log4j2
public class AttributeUpdaterKernel {

    ExecutorService executor = Executors.newFixedThreadPool(Config.getInstance().getThreadCount());
    MkvFileCollector collector = new MkvFileCollector();
    int filesChangedAmount = 0;
    int filesNotChangedAmount = 0;
    long runtime = 0;

    @SneakyThrows
    public void execute() {
        long beforeTimer = System.currentTimeMillis();

        collector.loadFiles(Config.getInstance().getLibraryPath())
                .forEach(file -> executor.submit(() -> process(file)));
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        runtime = System.currentTimeMillis() - beforeTimer;

        System.out.printf("%nFiles %schanged: %s%n",
                Config.getInstance().isSafeMode() ? "would " : "",
                filesChangedAmount);
        System.out.printf("Files %s not changed: %s%n",
                Config.getInstance().isSafeMode() ? "would " : "",
                filesNotChangedAmount);
        System.out.printf("Runtime: %ss%n", runtime / 1000);
    }

    private void process(File file) {
        List<FileAttribute> attributes = collector.loadAttributes(file);
        boolean fileHasChanged = false;

        if (attributes.isEmpty()) return;
        for(AttributeConfig config : Config.getInstance().getAttributeConfig()){
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

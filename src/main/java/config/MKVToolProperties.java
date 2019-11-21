package config;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class MKVToolProperties {
    private String directoryPath;
    private Path mkvmergePath;
    private Path mkvpropeditPath;

    public MKVToolProperties() {
        try(Stream<String> stream = Files.lines(Paths.get("mkvDirectoryPath"))) {
            directoryPath = stream.collect(Collectors.joining("\n"));
        }catch(IOException e) {
            log.fatal(e.getMessage());
        }

        mkvmergePath = Paths.get(directoryPath + "mkvmerge.exe");
        mkvpropeditPath = Paths.get(directoryPath + "mkvpropedit.exe");
    }

}

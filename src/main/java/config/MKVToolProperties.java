package config;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class MKVToolProperties {
    private String directoryPath;
    private String mkvmergePath;
    private String mkvpropeditPath;

    public MKVToolProperties() {
        try(Stream<String> stream = Files.lines(Paths.get("mkvDirectoryPath"))) {
            directoryPath = stream.collect(Collectors.joining("\n"));
        }catch(IOException e) {
            log.fatal(e.getMessage());
        }

        if(!directoryPath.endsWith("\\")) {
            directoryPath += "\\";
        }
        mkvmergePath = directoryPath + "mkvmerge.exe";
        mkvpropeditPath = directoryPath + "mkvpropedit.exe";
    }


    public boolean pathsAreValid() {
        File mkvmergeFile = new File(mkvmergePath);
        File mkvpropeditFile = new File(mkvpropeditPath);

        return mkvmergeFile.exists() && mkvpropeditFile.exists();
    }
}

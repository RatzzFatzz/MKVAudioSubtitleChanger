package model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Getter
public class FileAttribute {
    private int id;
    private String language;
    private String trackName;
    private boolean defaultTrack;
    private boolean forcedTrack;
    private String type;

    public FileAttribute(int id, String language, String trackName, boolean defaultTrack, boolean forcedTrack, String type) {
        this.id = id;
        this.language = language;
        this.trackName = trackName;
        this.defaultTrack = defaultTrack;
        this.forcedTrack = forcedTrack;
        this.type = type;
    }

    public static boolean pathIsValid(String path) {
        File file = new File(path);
        if(file.isFile()){
            return file.getAbsolutePath().endsWith(".mkv");
        }
        if(file.isDirectory()){
            try(Stream<Path> paths = Files.walk(Paths.get(path))){
                List<String> allPaths = paths
                        .filter(Files::isRegularFile)
                        .map(f -> f.toAbsolutePath().toString())
                        .collect(Collectors.toList());
                for(String filePath : allPaths){
                    if(! filePath.endsWith(".mkv")){
                        return false;
                    }
                }
            }catch(IOException e){
                log.error("Couldn't find file or directory!", e);
            }
            return true;
        }
        return false;
    }
}

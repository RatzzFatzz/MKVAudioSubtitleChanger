package config;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class MKVToolProperties {
    private String directoryPath;
    private String mkvmergePath;
    private String mkvpropeditPath;

    private static MKVToolProperties instance = null;

    private MKVToolProperties() {
    }

    public static MKVToolProperties getInstance() {
        if(instance == null){
            instance = new MKVToolProperties();
        }
        return instance;
    }

    public void defineMKVToolNixPath() {
        searchWithFilePath();
        if(pathIsValid()){
            log.info("MKVToolNix found!");
            return;
        }
        log.debug("MKVToolNix not found in file!");
        searchInDefaultPath();
        if(pathIsValid()){
            log.info("MKVToolNix found!");
            return;
        }
        log.debug("MKVToolNix not found in default path!");
        Scanner input = new Scanner(System.in);
        while(true){
            searchWithUserPath(input);
            if(pathIsValid()){
                log.info("MKVToolNix found!");
                break;
            }
        }
        log.error("MKVToolNix not found anywhere!");
    }

    private boolean pathIsValid() {
        checkForSeparator();
        setMKVmergeAndPropEditPath();
        return new File(mkvmergePath).exists() && new File(mkvpropeditPath).exists();
    }

    private void checkForSeparator() {
        if(! (directoryPath.endsWith("/") || directoryPath.endsWith("\\"))){
            directoryPath += File.separator;
        }
    }

    private void setMKVmergeAndPropEditPath() {
        mkvmergePath = directoryPath + "mkvmerge.exe";
        mkvpropeditPath = directoryPath + "mkvpropedit.exe";
    }

    private void searchWithFilePath() {
        try(Stream<String> stream = Files.lines(Paths.get("mkvDirectoryPath"))){
            directoryPath = stream.collect(Collectors.joining("\n"));
        }catch(IOException e){
            log.fatal(e.getMessage());
        }
    }

    private void searchInDefaultPath() {

    }

    private void searchWithUserPath(Scanner input) {
        directoryPath = input.nextLine();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Getter
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
        if(searchWithFilePath() && pathIsValid()){
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
                break;
            }
        }
        try(PrintWriter writer = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            writer.println(directoryPath);
        }catch(UnsupportedEncodingException | FileNotFoundException e){
            log.error("File counldn't be written!");
        }
        log.info("MKVToolNix found!");
    }

    private boolean pathIsValid() {
        checkForSeparator();
        setMKVmergeAndPropEditPath();
        return new File(mkvmergePath).exists() && new File(mkvpropeditPath).exists();
    }

    private void checkForSeparator() {
        if(! (directoryPath.endsWith("/") || (directoryPath.endsWith("\\") && System.getProperty("os.name").toLowerCase().contains("windows")))){
            directoryPath += File.separator;
        }
    }

    private void setMKVmergeAndPropEditPath() {
        mkvmergePath = directoryPath + "mkvmerge.exe";
        mkvpropeditPath = directoryPath + "mkvpropedit.exe";
    }

    private boolean searchWithFilePath() {
        try(Stream<String> stream = Files.lines(Paths.get("mkvDirectoryPath"))){
            directoryPath = stream.collect(Collectors.joining("\n"));
        }catch(IOException e){
            log.fatal(e.getMessage());
            return false;
        }
        return true;
    }

    private void searchInDefaultPath() {
        directoryPath = "C:\\Program Files\\MKVToolNix";
    }

    private void searchWithUserPath(Scanner input) {
        log.info("Please enter the path to the directory of MKVToolNix:");
        directoryPath = input.nextLine();
    }
}

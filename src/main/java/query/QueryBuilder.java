package query;

import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.MKVToolProperties;
import lombok.extern.log4j.Log4j2;
import model.FileAttribute;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class QueryBuilder {
    private ObjectMapper mapper = new ObjectMapper();

    public QueryBuilder() {
    }

    public boolean executeUpdateOnAllFiles(String path, JTextArea outputArea) {
        List<String> allFilePaths = getAllFilesFromDirectory(path);
        if(allFilePaths == null){
            log.error("Couldn't process path!");
            return false;
        }
        for(String filePath : allFilePaths){
            updateAttributes(filePath, queryAttributes(filePath));
            log.info("Success: " + filePath);
            System.out.println("Success: " + filePath);
        }
        return true;
    }

    private List<String> getAllFilesFromDirectory(String path) {
        try(Stream<Path> paths = Files.walk(Paths.get(path))){
            return paths
                    .filter(Files::isRegularFile)
                    .map(file -> file.toAbsolutePath().toString())
                    .collect(Collectors.toList());
        }catch(IOException e){
            log.error("Couldn't find file or directory!", e);
        }
        return null;
    }

    private List<FileAttribute> queryAttributes(String path) {
        Map<String, Object> jsonMap;
        List<FileAttribute> fileAttributes = new ArrayList<>();
        try(InputStream inputStream = Runtime.getRuntime().exec("\"" + MKVToolProperties.getInstance().getMkvmergePath() + "\" --identify --identification-format json \"" + path + "\"").getInputStream()){
            jsonMap = mapper.readValue(inputStream, Map.class);
            List<Map<String, Object>> tracks = (List<Map<String, Object>>) jsonMap.get("tracks");

            for(Map<String, Object> attribute : tracks){
                if(! "video".equals(attribute.get("type"))){
                    Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");
                    fileAttributes.add(new FileAttribute(
                            (int) properties.get("number"),
                            (String) properties.get("language"),
                            (String) properties.get("track_name"),
                            (Boolean) properties.get("default_track"),
                            (Boolean) properties.get("forced_track"),
                            (String) attribute.get("type")));
                }
            }
        }catch(IOException e){
            log.error("File could not be found or loaded!");
        }
        return fileAttributes;
    }

    private void updateAttributes(String path, List<FileAttribute> fileAttributes) {

        YAML yaml;
        List<String> subtitles = null;
        List<String> audios = null;

        try{
            yaml = new YAML(new File("src/main/resources/config.yaml"));
            subtitles = yaml.getStringList("subtitle", null);
            audios = yaml.getStringList("audio", null);
        }catch(YamlInvalidContentException | IOException e){
            log.error(e.getMessage());
        }

        if(fileAttributes.size() > 2 && subtitles != null && audios != null){


            int oldAudioDefault = - 1;
            int oldSubtitleDefault = - 1;
            int audioDefault = - 1;
            int subtitleDefault = - 1;
            int subtitleIndex = - 1;
            int audioIndex = - 1;

            for(FileAttribute attribute : fileAttributes){
                if(subtitles.contains(attribute.getLanguage()) && "subtitles".equals(attribute.getType())){
                    for(int i = 0; i < subtitles.size(); i++){
                        if(subtitles.get(i).equals(attribute.getLanguage())){
                            if(subtitleIndex == - 1 || i < subtitleIndex){
                                subtitleIndex = i;
                                subtitleDefault = attribute.getId();
                            }
                        }
                    }
                }
                if(audios.contains(attribute.getLanguage()) && "audio".equals(attribute.getType())){
                    for(int i = 0; i < audios.size(); i++){
                        if(audios.get(i).equals(attribute.getLanguage())){
                            if(audioIndex == - 1 || i < audioIndex){
                                audioIndex = i;
                                audioDefault = attribute.getId();
                            }
                        }
                    }
                }

                if(attribute.isDefaultTrack() && "audio".equals(attribute.getType())){
                    oldAudioDefault = attribute.getId();
                }
                if(attribute.isDefaultTrack() && "subtitles".equals(attribute.getType())){
                    oldSubtitleDefault = attribute.getId();
                }
            }
            StringBuilder stringBuffer = new StringBuilder("\"");
            stringBuffer.append(MKVToolProperties.getInstance().getMkvpropeditPath());
            stringBuffer.append("\" \"").append(path).append("\" ");
            stringBuffer.append("--edit track:").append(oldSubtitleDefault).append(" --set flag-default=0 ");
            stringBuffer.append("--edit track:").append(oldAudioDefault).append(" --set flag-default=0 ");
            stringBuffer.append("--edit track:").append(subtitleDefault).append(" --set flag-default=1 ");
            stringBuffer.append("--edit track:").append(audioDefault).append(" --set flag-default=1 ");

            try{
                Runtime.getRuntime().exec(stringBuffer.toString());
            }catch(IOException e){
                log.error("Couldn't make changes to file");

            }
        }else{
            log.info("There were not enough lines provided to make any changes to the file");
        }
    }
}

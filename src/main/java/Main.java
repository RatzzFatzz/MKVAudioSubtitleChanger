import com.fasterxml.jackson.databind.ObjectMapper;
import config.MKVToolProperties;
import lombok.extern.log4j.Log4j2;
import model.FileAttribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Log4j2
public class Main {
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        Scanner input = new Scanner(System.in);
        log.info("Please enter path to file");
        String path = input.nextLine();
        updateAttributes(path, queryAttributes(path));
    }

    public static List<FileAttribute> queryAttributes(String path) {
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

    public static void updateAttributes(String path, List<FileAttribute> fileAttributes) {
        if(fileAttributes.size() > 2){
            StringBuffer stringBuffer = new StringBuffer("\"");
            stringBuffer.append(MKVToolProperties.getInstance().getMkvpropeditPath());
            stringBuffer.append("\" \"");
            stringBuffer.append(path);
            stringBuffer.append("\" ");

            int c = 0;
            int d = 0;

            for(FileAttribute attributes : fileAttributes){
                if(attributes.isDefaultTrack() && "audio".equals(attributes.getType())){
                    stringBuffer.append("--edit track:" + attributes.getId() + " --set flag-default=0 ");
                }
                if(attributes.isDefaultTrack() && "subtitles".equals(attributes.getType())){
                    stringBuffer.append("--edit track:" + attributes.getId() + " --set flag-default=0 ");
                }
                if("jpn".equals(attributes.getLanguage()) && "audio".equals(attributes.getType()) && c == 0){
                    c++;
                    stringBuffer.append("--edit track:" + attributes.getId() + " --set flag-default=1 ");
                }
                if("eng".equals(attributes.getLanguage()) && "subtitles".equals(attributes.getType()) && d == 0){
                    d++;
                    stringBuffer.append("--edit track:" + attributes.getId() + " --set flag-default=1 ");
                }
            }
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

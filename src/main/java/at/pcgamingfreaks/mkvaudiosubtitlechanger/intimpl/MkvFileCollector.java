package at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.MKVToolProperties;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

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
public class MkvFileCollector implements FileCollector {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * @param path Is entered path, which leads to one file directly or a directory which will be loaded recursive
     * @return list of all files within the directory, if it's only a file, the file will be returned in a list
     */
    @Override
    public List<File> loadFiles(String path) {
        File file = new File(path);
        if (file.isFile() && file.getAbsolutePath().endsWith(".mkv")) {
            return new ArrayList<File>() {{
                add(file);
            }};
        } else if (file.isDirectory()) {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getAbsolutePath().endsWith(".mkv"))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Couldn't find file or directory!", e);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param file Takes the file from which the attributes will be returned
     * @return list of all important attributes
     */
    @Override
    public List<FileAttribute> loadAttributes(File file) {
        Map<String, Object> jsonMap;
        List<FileAttribute> fileAttributes = new ArrayList<>();
        try {
            String command = "";
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                command = "\"" + MKVToolProperties.getInstance().getMkvmergePath() + "\"";
            } else {
                command = MKVToolProperties.getInstance().getMkvmergePath();
            }
            String[] array = new String[]{
                    command,
                    "--identify",
                    "--identification-format",
                    "json",
                    file.getAbsoluteFile().toString()
            };

            InputStream inputStream = Runtime.getRuntime().exec(array).getInputStream();
            jsonMap = mapper.readValue(inputStream, Map.class);
            List<Map<String, Object>> tracks = (List<Map<String, Object>>) jsonMap.get("tracks");
            if (tracks == null) {
                log.warn("Couldn't retrieve information of {}", file.getAbsoluteFile().toString());
                return new ArrayList<>();
            }
            for (Map<String, Object> attribute : tracks) {
                if (!"video".equals(attribute.get("type"))) {
                    Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");
                    fileAttributes.add(new FileAttribute(
                            (int) properties.get("number"),
                            (String) properties.get("language"),
                            (String) properties.get("track_name"),
                            (Boolean) properties.getOrDefault("default_track", false),
                            (Boolean) properties.getOrDefault("forced_track", false),
                            (String) attribute.get("type")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("File could not be found or loaded!");
        }
        return fileAttributes;
    }
}

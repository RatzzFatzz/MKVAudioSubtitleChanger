package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class MkvFileCollector implements FileCollector {

    @Override
    public List<File> loadFiles(String path) {
        File file = new File(path);
        if (file.isFile() && file.getAbsolutePath().endsWith(".mkv")) {
            return new ArrayList<>() {{
                add(file);
            }};
        } else if (file.isDirectory()) {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getAbsolutePath().endsWith(".mkv"))
                        .filter(f -> Config.getInstance().getIncludePattern().matcher(f.getName()).matches())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Couldn't find file or directory!", e);
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }
}

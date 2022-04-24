package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

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
    private static final String[] fileExtensions = new String[]{".mkv", ".mka", ".mks", ".mk3d"};

    @Override
    public List<File> loadFiles(String path) {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> FileFilter.accept(file, fileExtensions))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Couldn't find file or directory!", e);
            return new ArrayList<>();
        }
    }
}

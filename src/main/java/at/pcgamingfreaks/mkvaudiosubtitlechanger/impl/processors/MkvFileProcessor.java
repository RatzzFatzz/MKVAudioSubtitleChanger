package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.FileFilter;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileUtils.getPathFor;

@Slf4j
@RequiredArgsConstructor
public class MkvFileProcessor implements FileProcessor {
    protected final File mkvToolNixInstallation;
    protected final FileFilter fileFilter;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final Set<String> fileExtensions = new HashSet<>(Set.of(".mkv", ".mka", ".mks", ".mk3d"));

    private static final String DEFAULT_TRACK = "--edit track:%s --set flag-default=%s";
    private static final String FORCED_TRACK = "--edit track:%s --set flag-forced=%s";
    private static final String COMMENTARY_TRACK = "--edit track:%s --set flag-commentary=%s";
    private static final String HEARING_IMPAIRED_TRACK = "--edit track:%s --set flag-hearing-impaired=%s";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> loadFiles(String path) {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> fileFilter.accept(file, fileExtensions))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Couldn't find file or directory!", e);
            return new ArrayList<>();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // does this load /arst/arst & /arst ?
    public List<File> loadDirectories(String path, int depth) {
        try (Stream<Path> paths = Files.walk(Paths.get(path), depth)) {
            return paths.map(Path::toFile)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Couldn't find file or directory!", e);
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileInfo readAttributes(File file) {
        FileInfo fileInfo = new FileInfo(file);
        try {
            String[] command = new String[]{
                    getPathFor(mkvToolNixInstallation, MkvToolNix.MKV_MERGE).getAbsolutePath(),
                    "--identify",
                    "--identification-format",
                    "json",
                    file.getAbsolutePath()
            };

            log.debug("Executing: {}", String.join(" ", command));
            InputStream inputStream = Runtime.getRuntime().exec(command)
                    .getInputStream();
            Map<String, Object> jsonMap = mapper.readValue(inputStream, Map.class);
            List<Map<String, Object>> tracks = (List<Map<String, Object>>) jsonMap.get("tracks");
            if (tracks != null) {
                for (Map<String, Object> attribute : tracks) {
                    if (!"video".equals(attribute.get("type"))) {
                        Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");
                        fileInfo.addTrack(new TrackAttributes(
                                (int) properties.get("number"),
                                (String) properties.get("language"),
                                (String) properties.get("track_name"),
                                (Boolean) properties.getOrDefault("default_track", false),
                                (Boolean) properties.getOrDefault("forced_track", false),
                                (Boolean) properties.getOrDefault("commentary_track", false),
                                (Boolean) properties.getOrDefault("hearing_impaired_track", false),
                                TrackType.valueOf(((String) attribute.get("type")).toUpperCase(Locale.ENGLISH))));
                    }
                }
            } else {
                log.warn("Couldn't retrieve information of {}", file.getAbsolutePath());
            }

            log.debug("File attributes of '{}': {}", file.getAbsolutePath(), fileInfo.getTracks());
        } catch (IOException e) {
            log.error("File could not be found or loaded: ", e);
            System.out.println("File could not be found or loaded: " + file.getAbsolutePath());
        }
        return fileInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(FileInfo fileInfo) throws IOException, MkvToolNixException {
        List<String> command = new ArrayList<>();
        command.add(getPathFor(mkvToolNixInstallation, MkvToolNix.MKV_PROP_EDIT).getAbsolutePath());
        command.add(String.format(fileInfo.getFile().getAbsolutePath()));

        PlannedChange changes = fileInfo.getChanges();
        changes.getDefaultTrack().forEach((key, value) -> command.addAll(format(DEFAULT_TRACK, key.id(), value ? 1 : 0)));
        changes.getForcedTrack().forEach((key, value) -> command.addAll(format(FORCED_TRACK, key.id(), value ? 1 : 0)));
        changes.getCommentaryTrack().forEach((key, value) -> command.addAll(format(COMMENTARY_TRACK, key.id(), value ? 1 : 0)));
        changes.getHearingImpairedTrack().forEach((key, value) -> command.addAll(format(HEARING_IMPAIRED_TRACK, key.id(), value ? 1 : 0)));

        log.debug("Executing '{}'", String.join(" ", command));
        InputStream inputstream = Runtime.getRuntime().exec(command.toArray(new String[0])).getInputStream();
        String output = IOUtils.toString(new InputStreamReader(inputstream));
        log.debug("Result: {}", output);
        if (output.contains("Error")) throw new MkvToolNixException(output);
    }

    private List<String> format(String format, Object... args) {
        return Arrays.asList(String.format(format, args).split(" "));
    }
}

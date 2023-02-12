package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.SetUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType.AUDIO;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType.SUBTITLES;
import static java.lang.String.format;

@Log4j2
public class MkvFileProcessor implements FileProcessor {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String[] forcedKeywords = new String[]{"forced", "signs"};
    private static final String DISABLE_DEFAULT_TRACK = "--edit track:%s --set flag-default=0 ";
    private static final String ENABLE_DEFAULT_TRACK = "--edit track:%s --set flag-default=1 ";
    private static final String ENABLE_FORCED_TRACK = "--edit track:%s --set flag-forced=1 ";


    @Override
    public List<FileAttribute> loadAttributes(File file) {
        Map<String, Object> jsonMap;
        List<FileAttribute> fileAttributes = new ArrayList<>();
        try {
            String command = format("\"%s\"", Config.getInstance().getPathFor(MkvToolNix.MKV_MERGER));
            String[] arguments = new String[]{
                    command,
                    "--identify",
                    "--identification-format",
                    "json",
                    file.getAbsoluteFile().toString()
            };

            InputStream inputStream = Runtime.getRuntime().exec(arguments).getInputStream();
            jsonMap = mapper.readValue(inputStream, Map.class);
            List<Map<String, Object>> tracks = (List<Map<String, Object>>) jsonMap.get("tracks");
            if (tracks == null) {
                log.warn("Couldn't retrieve information of {}", file.getAbsolutePath());
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
                            LaneType.valueOf(((String) attribute.get("type")).toUpperCase(Locale.ENGLISH))));
                }
            }

            log.debug(fileAttributes);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("File could not be found or loaded!");
        }
        return fileAttributes;
    }

    @Override
    public FileInfoDto filterAttributes(List<FileAttribute> attributes) {
        FileInfoDto info = new FileInfoDto();
        List<FileAttribute> nonForcedTracks = attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.getTrackName(),
                        Config.getInstance().getForcedKeywords().toArray(new CharSequence[0])))
                .filter(elem -> !elem.isForcedTrack())
                .collect(Collectors.toList());
        List<FileAttribute> nonCommentaryTracks = attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.getTrackName(),
                        Config.getInstance().getCommentaryKeywords().toArray(new CharSequence[0])))
                .collect(Collectors.toList());

        detectDefaultTracks(info, attributes, nonForcedTracks);
        detectDesiredTracks(info, nonForcedTracks, nonCommentaryTracks);
        log.debug(info);

        return info;
    }

    protected void detectDefaultTracks(FileInfoDto info, List<FileAttribute> attributes, List<FileAttribute> nonForcedTracks) {
        Set<FileAttribute> detectedForcedSubtitleLanes = new HashSet<>();
        for (FileAttribute attribute : attributes) {
            if (attribute.isDefaultTrack() && AUDIO.equals(attribute.getType()))
                info.getDefaultAudioLanes().add(attribute);
            if (attribute.isDefaultTrack() && SUBTITLES.equals(attribute.getType()))
                info.getDefaultSubtitleLanes().add(attribute);
            if (attribute.isForcedTrack() && SUBTITLES.equals(attribute.getType()))
                detectedForcedSubtitleLanes.add(attribute);
        }

        info.setDesiredForcedSubtitleLanes(attributes.stream()
                .filter(e -> !nonForcedTracks.contains(e))
                .filter(e -> !detectedForcedSubtitleLanes.contains(e))
                .collect(Collectors.toSet())
        );
    }

    protected void detectDesiredTracks(FileInfoDto info, List<FileAttribute> nonForcedTracks, List<FileAttribute> nonCommentaryTracks) {
        for (AttributeConfig config : Config.getInstance().getAttributeConfig()) {
            FileAttribute desiredAudio = null;
            FileAttribute desiredSubtitle = null;
            for (FileAttribute attribute : SetUtils.retainOf(nonForcedTracks, nonCommentaryTracks)) {
                if (attribute.getLanguage().equals(config.getAudioLanguage())
                        && AUDIO.equals(attribute.getType())) desiredAudio = attribute;
                if (attribute.getLanguage().equals(config.getSubtitleLanguage())
                        && SUBTITLES.equals(attribute.getType())) desiredSubtitle = attribute;
            }
            if (desiredAudio != null && desiredSubtitle != null) {
                info.setDesiredAudioLane(desiredAudio);
                info.setDesiredSubtitleLane(desiredSubtitle);
                break;
            }
        }
    }

    @Override
    public void update(File file, FileInfoDto fileInfo) throws IOException, RuntimeException {
        StringBuilder sb = new StringBuilder();
        sb.append(format("\"%s\" ", Config.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT)));
        sb.append(format("\"%s\" ", file.getAbsolutePath()));
        if (fileInfo.isAudioDifferent()) {
            if (fileInfo.getDefaultAudioLanes() != null && !fileInfo.getDefaultSubtitleLanes().isEmpty()) {
                for (FileAttribute track: fileInfo.getDefaultAudioLanes()) {
                    sb.append(format(DISABLE_DEFAULT_TRACK, track.getId()));
                }
            }
            sb.append(format(ENABLE_DEFAULT_TRACK, fileInfo.getDesiredAudioLane().getId()));
        }
        if (fileInfo.isSubtitleDifferent()) {
            if (fileInfo.getDefaultSubtitleLanes() != null && !fileInfo.getDefaultSubtitleLanes().isEmpty()) {
                for (FileAttribute track: fileInfo.getDefaultSubtitleLanes()) {
                    sb.append(format(DISABLE_DEFAULT_TRACK, track.getId()));
                }
            }
            sb.append(format(ENABLE_DEFAULT_TRACK, fileInfo.getDesiredSubtitleLane().getId()));
        }
        if (fileInfo.areForcedTracksDifferent()) {
            for (FileAttribute attribute : fileInfo.getDesiredForcedSubtitleLanes()) {
                sb.append(format(ENABLE_FORCED_TRACK, attribute.getId()));
            }
        }

        InputStream inputstream = Runtime.getRuntime().exec(sb.toString()).getInputStream();
        String output = IOUtils.toString(new InputStreamReader(inputstream));
        if (output.contains("Error")) throw new RuntimeException(output);
        log.debug(output);
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType.AUDIO;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType.SUBTITLES;

@Log4j2
public class MkvFileProcessor implements FileProcessor {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<FileAttribute> loadAttributes(File file) {
        Map<String, Object> jsonMap;
        List<FileAttribute> fileAttributes = new ArrayList<>();
        try {
            String command = String.format("\"%s\"", Config.getInstance().getPathFor(MkvToolNix.MKV_MERGER));
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
                            LaneType.valueOf(((String) attribute.get("type")).toUpperCase(Locale.ENGLISH))));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("File could not be found or loaded!");
        }
        return fileAttributes;
    }

    @Override
    public FileInfoDto filterAttributes(List<FileAttribute> attributes) {
        FileInfoDto info = new FileInfoDto();
        for (FileAttribute attribute: attributes) {
            if (attribute.isDefaultTrack() && AUDIO.equals(attribute.getType())) info.setDefaultAudioLane(attribute);
            if (attribute.isForcedTrack() && AUDIO.equals(attribute.getType())) info.setForcedAudioLane(attribute);

            if (attribute.isDefaultTrack() && SUBTITLES.equals(attribute.getType())) info.setDefaultSubtitleLane(attribute);
            if (attribute.isForcedTrack() && SUBTITLES.equals(attribute.getType())) info.setForcedSubtitleLane(attribute);
        }
        List<FileAttribute> nonForcedTracks = attributes.stream()
                .filter(elem -> !StringUtils.containsIgnoreCase(elem.getTrackName(), "forced"))
                .collect(Collectors.toList());
        for (AttributeConfig config: Config.getInstance().getAttributeConfig()) {
            FileAttribute desiredAudio = null;
            FileAttribute desiredSubtitle = null;
            for (FileAttribute attribute: nonForcedTracks) {
                if ( attribute.getLanguage().equals(config.getAudioLanguage())
                    && AUDIO.equals(attribute.getType())) desiredAudio = attribute;
                if ( attribute.getLanguage().equals(config.getSubtitleLanguage())
                    && SUBTITLES.equals(attribute.getType())) desiredSubtitle = attribute;
            }
            if (desiredAudio != null && desiredSubtitle != null) {
                info.setDesiredAudioLane(desiredAudio);
                info.setDesiredSubtitleLane(desiredSubtitle);
                break;
            }
        }
        return info;
    }

    @Override
    public void update(File file, FileInfoDto fileInfo) {

    }
}

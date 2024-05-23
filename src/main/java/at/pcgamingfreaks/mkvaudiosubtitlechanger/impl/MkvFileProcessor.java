package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions.MkvToolNixException;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.SetUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType.AUDIO;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType.SUBTITLES;
import static java.lang.String.format;

@Slf4j
public class MkvFileProcessor implements FileProcessor {
    private final ObjectMapper mapper = new ObjectMapper();

    private static final SubtitleTrackComparator subtitleTrackComparator =
            new SubtitleTrackComparator(Config.getInstance().getPreferredSubtitles().toArray(new String[0]));

    private static final String DISABLE_DEFAULT_TRACK = "--edit track:%s --set flag-default=0 ";
    private static final String ENABLE_DEFAULT_TRACK = "--edit track:%s --set flag-default=1 ";
    private static final String DISABLE_FORCED_TRACK = "--edit track:%s --set flag-forced=0 ";
    private static final String ENABLE_FORCED_TRACK = "--edit track:%s --set flag-forced=1 ";

    @SuppressWarnings("unchecked")
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

            log.debug(fileAttributes.toString());
        } catch (IOException e) {
            log.error("File could not be found or loaded: ", e);
            System.out.println("File could not be found or loaded: " + file.getAbsolutePath());
        }
        return fileAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detectDefaultTracks(FileInfoDto info, List<FileAttribute> attributes, List<FileAttribute> nonForcedTracks) {
        for (FileAttribute attribute : attributes) {
            if (AUDIO.equals(attribute.getType())) {
                if (attribute.isDefaultTrack()) info.getExistingDefaultAudioLanes().add(attribute);
                if (attribute.isForcedTrack()) info.getExistingForcedAudioLanes().add(attribute);
            } else if (SUBTITLES.equals(attribute.getType())) {
                if (attribute.isDefaultTrack()) info.getExistingDefaultSubtitleLanes().add(attribute);

                if (attribute.isForcedTrack()) info.getExistingForcedSubtitleLanes().add(attribute);
                else if (!nonForcedTracks.contains(attribute)) info.getDesiredForcedSubtitleLanes().add(attribute);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detectDesiredTracks(FileInfoDto info, List<FileAttribute> nonForcedTracks, List<FileAttribute> nonCommentaryTracks,
                                    AttributeConfig... configs) {
        Set<FileAttribute> tracks = SetUtils.retainOf(nonForcedTracks, nonCommentaryTracks);
        Set<FileAttribute> audioTracks = tracks.stream().filter(a -> AUDIO.equals(a.getType())).collect(Collectors.toSet());
        Set<FileAttribute> subtitleTracks = tracks.stream().filter(a -> SUBTITLES.equals(a.getType())).collect(Collectors.toSet());

        for (AttributeConfig config : configs) {
            Optional<FileAttribute> desiredAudio = detectDesiredTrack(config.getAudioLanguage(), audioTracks).findFirst();
            Optional<FileAttribute> desiredSubtitle = detectDesiredSubtitleTrack(config.getSubtitleLanguage(), subtitleTracks).findFirst();

            if (desiredAudio.isPresent() && ("OFF".equals(config.getSubtitleLanguage()) || desiredSubtitle.isPresent())) {
                info.setMatchedConfig(config);
                info.setDesiredDefaultAudioLane(desiredAudio.get());
                info.setDesiredDefaultSubtitleLane(desiredSubtitle.orElse(null));
                break;
            }
        }
    }

    private Stream<FileAttribute> detectDesiredTrack(String language, Set<FileAttribute> tracks) {
        return tracks.stream().filter(track -> language.equals(track.getLanguage()));
    }

    private Stream<FileAttribute> detectDesiredSubtitleTrack(String language, Set<FileAttribute> tracks) {
        return detectDesiredTrack(language, tracks)
                .sorted(subtitleTrackComparator.reversed());
    }

    @Override
    public List<FileAttribute> retrieveNonForcedTracks(List<FileAttribute> attributes) {
        return attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.getTrackName(),
                        Config.getInstance().getForcedKeywords().toArray(new CharSequence[0])))
                .filter(elem -> !elem.isForcedTrack())
                .collect(Collectors.toList());
    }

    @Override
    public List<FileAttribute> retrieveNonCommentaryTracks(List<FileAttribute> attributes) {
        return attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.getTrackName(),
                        Config.getInstance().getCommentaryKeywords().toArray(new CharSequence[0])))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(File file, FileInfoDto fileInfo) throws IOException, MkvToolNixException {
        StringBuilder sb = new StringBuilder();
        sb.append(format("\"%s\" ", Config.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT)));
        sb.append(format("\"%s\" ", file.getAbsolutePath()));

        if (fileInfo.isAudioDifferent()) {
            if (fileInfo.getExistingDefaultAudioLanes() != null && !fileInfo.getExistingDefaultAudioLanes().isEmpty()) {
                for (FileAttribute track: fileInfo.getExistingDefaultAudioLanes()) {
                    sb.append(format(DISABLE_DEFAULT_TRACK, track.getId()));
                }
            }
            sb.append(format(ENABLE_DEFAULT_TRACK, fileInfo.getDesiredDefaultAudioLane().getId()));
        }

        if (!fileInfo.getExistingForcedAudioLanes().isEmpty()) {
            for (FileAttribute track: fileInfo.getExistingForcedAudioLanes()) {
                sb.append(format(DISABLE_FORCED_TRACK, track.getId()));
            }
        }

        if (fileInfo.isSubtitleDifferent()) {
            if (fileInfo.getExistingDefaultSubtitleLanes() != null && !fileInfo.getExistingDefaultSubtitleLanes().isEmpty()) {
                for (FileAttribute track: fileInfo.getExistingDefaultSubtitleLanes()) {
                    sb.append(format(DISABLE_DEFAULT_TRACK, track.getId()));
                }
            }
            if (fileInfo.getDesiredDefaultSubtitleLane() != null) {
                sb.append(format(ENABLE_DEFAULT_TRACK, fileInfo.getDesiredDefaultSubtitleLane().getId()));
            }
        }

        if (fileInfo.areForcedTracksDifferent()) {
            for (FileAttribute attribute : fileInfo.getDesiredForcedSubtitleLanes()) {
                sb.append(format(ENABLE_FORCED_TRACK, attribute.getId()));
            }
        }

        log.info(sb.toString());
        InputStream inputstream = Runtime.getRuntime().exec(sb.toString()).getInputStream();
        String output = IOUtils.toString(new InputStreamReader(inputstream));
        log.debug(output);
        if (output.contains("Error")) throw new MkvToolNixException(output);
    }
}

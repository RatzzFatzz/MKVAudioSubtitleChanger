package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.InputConfig;
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
            new SubtitleTrackComparator(InputConfig.getInstance().getPreferredSubtitles().toArray(new String[0]));

    private static final String DISABLE_DEFAULT_TRACK = "--edit track:%s --set flag-default=0";
    private static final String ENABLE_DEFAULT_TRACK = "--edit track:%s --set flag-default=1";
    private static final String DISABLE_FORCED_TRACK = "--edit track:%s --set flag-forced=0";
    private static final String ENABLE_FORCED_TRACK = "--edit track:%s --set flag-forced=1";

    @SuppressWarnings("unchecked")
    @Override
    public List<FileAttribute> loadAttributes(File file) {
        Map<String, Object> jsonMap;
        List<FileAttribute> fileAttributes = new ArrayList<>();
        try {
            String[] command = new String[]{
                    InputConfig.getInstance().getPathFor(MkvToolNix.MKV_MERGE),
                    "--identify",
                    "--identification-format",
                    "json",
                    file.getAbsolutePath()
            };

            log.debug("Executing '{}': {}", file.getAbsolutePath(), String.join(" ", command));
            InputStream inputStream = Runtime.getRuntime().exec(command)
                    .getInputStream();
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

            log.debug("File attributes of '{}': {}", file.getAbsolutePath(), fileAttributes.toString());
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
    public void detectDefaultTracks(FileInfo info, List<FileAttribute> attributes, List<FileAttribute> nonForcedTracks) {
        for (FileAttribute attribute : attributes) {
            if (AUDIO.equals(attribute.type())) {
                if (attribute.defaultTrack()) info.getExistingDefaultAudioLanes().add(attribute);
                if (attribute.forcedTrack()) info.getExistingForcedAudioLanes().add(attribute);
            } else if (SUBTITLES.equals(attribute.type())) {
                if (attribute.defaultTrack()) info.getExistingDefaultSubtitleLanes().add(attribute);

                if (attribute.forcedTrack()) info.getExistingForcedSubtitleLanes().add(attribute);
                else if (!nonForcedTracks.contains(attribute)) info.getDesiredForcedSubtitleLanes().add(attribute);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detectDesiredTracks(FileInfo info, List<FileAttribute> nonForcedTracks, List<FileAttribute> nonCommentaryTracks,
                                    AttributeConfig... configs) {
        Set<FileAttribute> tracks = SetUtils.retainOf(nonForcedTracks, nonCommentaryTracks);
        Set<FileAttribute> audioTracks = tracks.stream().filter(a -> AUDIO.equals(a.type())).collect(Collectors.toSet());
        Set<FileAttribute> subtitleTracks = tracks.stream().filter(a -> SUBTITLES.equals(a.type())).collect(Collectors.toSet());

        for (AttributeConfig config : configs) {
            Optional<FileAttribute> desiredAudio = detectDesiredTrack(config.getAudioLanguage(), audioTracks).findFirst();
            Optional<FileAttribute> desiredSubtitle = detectDesiredSubtitleTrack(config.getSubtitleLanguage(), subtitleTracks).findFirst();

            if (("OFF".equals(config.getAudioLanguage()) || desiredAudio.isPresent())
                    && ("OFF".equals(config.getSubtitleLanguage()) || desiredSubtitle.isPresent())) {
                info.setMatchedConfig(config);
                info.setDesiredDefaultAudioLane(desiredAudio.orElse(null));
                info.setDesiredDefaultSubtitleLane(desiredSubtitle.orElse(null));
                break;
            }
        }
    }

    private Stream<FileAttribute> detectDesiredTrack(String language, Set<FileAttribute> tracks) {
        return tracks.stream().filter(track -> language.equals(track.language()));
    }

    private Stream<FileAttribute> detectDesiredSubtitleTrack(String language, Set<FileAttribute> tracks) {
        return detectDesiredTrack(language, tracks)
                .sorted(subtitleTrackComparator.reversed());
    }

    @Override
    public List<FileAttribute> retrieveNonForcedTracks(List<FileAttribute> attributes) {
        return attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.trackName(),
                        InputConfig.getInstance().getForcedKeywords().toArray(new CharSequence[0])))
                .filter(elem -> !elem.forcedTrack())
                .collect(Collectors.toList());
    }

    @Override
    public List<FileAttribute> retrieveNonCommentaryTracks(List<FileAttribute> attributes) {
        return attributes.stream()
                .filter(elem -> !StringUtils.containsAnyIgnoreCase(elem.trackName(),
                        InputConfig.getInstance().getCommentaryKeywords().toArray(new CharSequence[0])))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(File file, FileInfo fileInfo) throws IOException, MkvToolNixException {
        List<String> command = new ArrayList<>();
        command.add(InputConfig.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT));
        command.add(String.format(file.getAbsolutePath()));

        if (fileInfo.isAudioDifferent()) {
            removeExistingAndAddDesiredLanes(fileInfo.getExistingDefaultAudioLanes(), fileInfo.getDesiredDefaultAudioLane(), command);
        }

        if (!fileInfo.getExistingForcedAudioLanes().isEmpty()) {
            for (FileAttribute track : fileInfo.getExistingForcedAudioLanes()) {
                command.addAll(format(DISABLE_FORCED_TRACK, track.id()));
            }
        }

        if (fileInfo.isSubtitleDifferent()) {
            removeExistingAndAddDesiredLanes(fileInfo.getExistingDefaultSubtitleLanes(), fileInfo.getDesiredDefaultSubtitleLane(), command);
        }

        if (fileInfo.areForcedTracksDifferent()) {
            for (FileAttribute track : fileInfo.getDesiredForcedSubtitleLanes()) {
                command.addAll(format(ENABLE_FORCED_TRACK, track.id()));
            }
        }

        log.debug("Executing '{}'", String.join(" ", command));
        InputStream inputstream = Runtime.getRuntime().exec(command.toArray(new String[0])).getInputStream();
        String output = IOUtils.toString(new InputStreamReader(inputstream));
        log.debug("Result: {}", output);
        if (output.contains("Error")) throw new MkvToolNixException(output);
    }

    private void removeExistingAndAddDesiredLanes(Set<FileAttribute> existingDefaultLanes, FileAttribute desiredDefaultLanes, List<String> command) {
        if (existingDefaultLanes != null && !existingDefaultLanes.isEmpty()) {
            for (FileAttribute track : existingDefaultLanes) {
                command.addAll(format(DISABLE_DEFAULT_TRACK, track.id()));
            }
        }
        if (desiredDefaultLanes != null) {
            command.addAll(format(ENABLE_DEFAULT_TRACK, desiredDefaultLanes.id()));
        }
    }

    private List<String> format(String format, Object... args) {
        return Arrays.asList(String.format(format, args).split(" "));
    }
}

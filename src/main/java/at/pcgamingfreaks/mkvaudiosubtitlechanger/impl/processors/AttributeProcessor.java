package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.SubtitleTrackComparator;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AttributeProcessor {
    private static final SubtitleTrackComparator subtitleTrackComparator =
            new SubtitleTrackComparator(InputConfig.getInstance().getPreferredSubtitles().toArray(new String[0]));

    private static List<TrackAttributes> filterForPossibleDefaults(List<TrackAttributes> tracks) {
        InputConfig config = InputConfig.getInstance();
        Stream<TrackAttributes> attributes = tracks.stream();

        if (true) { // TODO: config for including commentary
            attributes = attributes
                    .filter(attr -> !attr.commentary())
                    .filter(attr -> {
                        if (attr.trackName() == null) return true;
                        return config.getCommentaryKeywords().stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                    });

        }

        if (true) { // TODO: config for including hearing impaired
            attributes = attributes
                    .filter(attr -> !attr.hearingImpaired())
                    .filter(attr -> {
                        if (attr.trackName() == null) return true;
                        return config.getHearingImpaired().stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                    });;
        }

        return attributes
                .filter(attr -> !attr.forced())
                .filter(attr -> {
                    if (attr.trackName() == null) return true;
                    return config.getForcedKeywords().stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                })
                .toList();
    }

    public static void findDefaultMatchAndApplyChanges(FileInfo fileInfo) {
        findDefaultMatchAndApplyChanges(fileInfo, InputConfig.getInstance().getAttributeConfig().toArray(new AttributeConfig[0]));
    }

    public static void findDefaultMatchAndApplyChanges(FileInfo fileInfo, AttributeConfig... configs) {
        Map<String, List<TrackAttributes>> audiosByLanguage = new HashMap<>(fileInfo.getTracks().size());
        Map<String, List<TrackAttributes>> subsByLanguage = new HashMap<>(fileInfo.getTracks().size());
        filterForPossibleDefaults(fileInfo.getTracks()).forEach(track -> {
            if (TrackType.AUDIO.equals(track.type()))
                audiosByLanguage.computeIfAbsent(track.language(), (k) -> new ArrayList<>()).add(track);
            else if (TrackType.SUBTITLES.equals(track.type()))
                subsByLanguage.computeIfAbsent(track.language(), (k) -> new ArrayList<>()).add(track);
        });

        for (AttributeConfig config : configs) {
            if (("OFF".equals(config.getAudioLanguage()) || audiosByLanguage.containsKey(config.getAudioLanguage()))
                    && ("OFF".equals(config.getSubtitleLanguage()) || subsByLanguage.containsKey(config.getSubtitleLanguage()))) {
                fileInfo.setMatchedConfig(config);
                break;
            }
            // TODO: forced if OFF
        }

        if (fileInfo.getMatchedConfig() == null) return;

        applyDefaultChanges(fileInfo, FileInfo::getAudioTracks, fileInfo.getMatchedConfig().getAudioLanguage(),
                () -> audiosByLanguage.get(fileInfo.getMatchedConfig().getAudioLanguage()).get(0));
        applyDefaultChanges(fileInfo, FileInfo::getSubtitleTracks, fileInfo.getMatchedConfig().getSubtitleLanguage(),
                () -> subsByLanguage.get(fileInfo.getMatchedConfig().getSubtitleLanguage()).stream().max(subtitleTrackComparator).get());
    }

    private static void applyDefaultChanges(FileInfo fileInfo, Function<FileInfo, List<TrackAttributes>> tracks, String language, Supplier<TrackAttributes> targetDefaultSupplier) {
        tracks.apply(fileInfo).stream()
                .filter(TrackAttributes::defaultt)
                .forEach(attr -> fileInfo.getChanges().getDefaultTrack().put(attr, false));
        if (!"OFF".equals(language)) {
            TrackAttributes targetDefault = targetDefaultSupplier.get();
            if (fileInfo.getChanges().getDefaultTrack().containsKey(targetDefault)) {
                fileInfo.getChanges().getDefaultTrack().remove(targetDefault);
            } else {
                fileInfo.getChanges().getDefaultTrack().put(targetDefault, true);
            }
        }
    }

    public static void findForcedTracksAndApplyChanges(FileInfo fileInfo) {
        Stream<TrackAttributes> forcedTracks = fileInfo.getTracks().stream()
                .filter(track -> track.trackName() != null)
                .filter(track -> InputConfig.getInstance().getForcedKeywords().stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))));

        if (InputConfig.getInstance().isOverwriteForced()) {
            fileInfo.getTracks().stream().filter(TrackAttributes::forced).forEach(attr -> {
                fileInfo.getChanges().getForcedTrack().put(attr, false);
            });
        } else {
            forcedTracks = forcedTracks.filter(attr -> !attr.forced());
        }

        forcedTracks.forEach(attr -> {
            fileInfo.getChanges().getForcedTrack().put(attr, true);
        });
    }

    public static void findCommentaryTracksAndApplyChanges(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(track -> !track.commentary())
                .filter(track -> track.trackName() != null)
                .filter(track -> InputConfig.getInstance().getCommentaryKeywords().stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))))
                .forEach(attr -> {
                    fileInfo.getChanges().getCommentaryTrack().put(attr, true);
                });
    }

    public static void findHearingImpairedTracksAndApplyChanges(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(track -> !track.commentary())
                .filter(track -> track.trackName() != null)
                .filter(track -> InputConfig.getInstance().getHearingImpaired().stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))))
                .forEach(attr -> {
                    fileInfo.getChanges().getHearingImpairedTrack().put(attr, true);
                });
    }
}

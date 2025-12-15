package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.SubtitleTrackComparator;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AttributeChangeProcessor {
    private final SubtitleTrackComparator subtitleTrackComparator;
    private final Set<String> commentaryKeywords;
    private final Set<String> hearingImpairedKeywords;
    private final Set<String> forcedKeywords;

    public AttributeChangeProcessor(String[] preferredSubtitles, Set<String> forcedKeywords, Set<String> commentaryKeywords, Set<String> hearingImpairedKeywords) {
        this.subtitleTrackComparator = new SubtitleTrackComparator(preferredSubtitles);
        this.commentaryKeywords = commentaryKeywords;
        this.hearingImpairedKeywords = hearingImpairedKeywords;
        this.forcedKeywords = forcedKeywords;
    }

    private List<TrackAttributes> filterForPossibleDefaults(List<TrackAttributes> tracks) {
        Stream<TrackAttributes> attributes = tracks.stream();

        if (true) { // TODO: config for including commentary
            attributes = attributes
                    .filter(attr -> !attr.commentary())
                    .filter(attr -> {
                        if (attr.trackName() == null) return true;
                        return commentaryKeywords.stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                    });

        }

        if (true) { // TODO: config for including hearing impaired
            attributes = attributes
                    .filter(attr -> !attr.hearingImpaired())
                    .filter(attr -> {
                        if (attr.trackName() == null) return true;
                        return hearingImpairedKeywords.stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                    });;
        }

        return attributes
                .filter(attr -> !attr.forced())
                .filter(attr -> {
                    if (attr.trackName() == null) return true;
                    return forcedKeywords.stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                })
                .toList();
    }

    public void findDefaultMatchAndApplyChanges(FileInfo fileInfo, AttributeConfig... configs) {
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

    private void applyDefaultChanges(FileInfo fileInfo, Function<FileInfo, List<TrackAttributes>> tracks, String language, Supplier<TrackAttributes> targetDefaultSupplier) {
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

    public void findForcedTracksAndApplyChanges(FileInfo fileInfo, boolean overwrite) {
        Stream<TrackAttributes> forcedTracks = fileInfo.getTracks().stream()
                .filter(track -> track.trackName() != null)
                .filter(track -> forcedKeywords.stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))));

        if (overwrite) {
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

    public void findCommentaryTracksAndApplyChanges(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(track -> !track.commentary())
                .filter(track -> track.trackName() != null)
                .filter(track -> commentaryKeywords.stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))))
                .forEach(attr -> {
                    fileInfo.getChanges().getCommentaryTrack().put(attr, true);
                });
    }

    public void findHearingImpairedTracksAndApplyChanges(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(track -> !track.commentary())
                .filter(track -> track.trackName() != null)
                .filter(track -> hearingImpairedKeywords.stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))))
                .forEach(attr -> {
                    fileInfo.getChanges().getHearingImpairedTrack().put(attr, true);
                });
    }
}

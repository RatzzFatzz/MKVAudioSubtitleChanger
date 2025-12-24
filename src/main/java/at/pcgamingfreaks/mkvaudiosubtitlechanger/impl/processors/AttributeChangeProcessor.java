package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.SubtitleTrackComparator;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;

import java.util.*;
import java.util.stream.Stream;

public class AttributeChangeProcessor {
    private final SubtitleTrackComparator subtitleTrackComparator;
    private final Set<String> commentaryKeywords;
    private final Set<String> hearingImpairedKeywords;
    private final Set<String> forcedKeywords;

    public AttributeChangeProcessor(String[] preferredSubtitles, Set<String> forcedKeywords, Set<String> commentaryKeywords, Set<String> hearingImpairedKeywords) {
        this.subtitleTrackComparator = new SubtitleTrackComparator(Arrays.stream(preferredSubtitles).toList(), hearingImpairedKeywords);
        this.commentaryKeywords = commentaryKeywords;
        this.hearingImpairedKeywords = hearingImpairedKeywords;
        this.forcedKeywords = forcedKeywords;
    }

    /**
     * Looks for default matches and applies them if found.
     */
    public void findAndApplyDefaultMatch(FileInfo fileInfo, AttributeConfig... configs) {
        Map<String, List<TrackAttributes>> audiosByLanguage = new HashMap<>(fileInfo.getTracks().size());
        Map<String, List<TrackAttributes>> subsByLanguage = new HashMap<>(fileInfo.getTracks().size());
        getPossibleDefaults(fileInfo.getTracks()).forEach(track -> {
            if (TrackType.AUDIO.equals(track.type()))
                audiosByLanguage.computeIfAbsent(track.language(), (k) -> new ArrayList<>()).add(track);
            else if (TrackType.SUBTITLES.equals(track.type()))
                subsByLanguage.computeIfAbsent(track.language(), (k) -> new ArrayList<>()).add(track);
        });

        for (AttributeConfig config : configs) {
            if (("OFF".equals(config.getAudioLang()) || audiosByLanguage.containsKey(config.getAudioLang()))
                    && ("OFF".equals(config.getSubLang()) || subsByLanguage.containsKey(config.getSubLang()))) {
                fileInfo.setMatchedConfig(config);
                break;
            }
        }

        if (fileInfo.getMatchedConfig() == null) return;

        AttributeConfig match = fileInfo.getMatchedConfig();
        removeExistingDefaults(fileInfo);
        if (!"OFF".equals(match.getAudioLang())) applyNewDefault(fileInfo, audiosByLanguage.get(fileInfo.getMatchedConfig().getAudioLang()).get(0));
        if (!"OFF".equals(match.getSubLang())) applyNewDefault(fileInfo, subsByLanguage.get(fileInfo.getMatchedConfig().getSubLang()).stream().max(subtitleTrackComparator).get());
    }

    /**
     * If match with xxx:OFF was found forced track in audio language is applied as default.
     * Forced track detection takes changes of {@link AttributeChangeProcessor#findAndApplyForcedTracks} into consideration.
     */
    public void applyForcedAsDefault(FileInfo fileInfo) {
        AttributeConfig c = fileInfo.getMatchedConfig();
        if (c == null) return;
        if (!"OFF".equals(c.getAudioLang()) && "OFF".equals(c.getSubLang())) {
            getForcedTracks(fileInfo)
                    .filter(track -> c.getAudioLang().equals(track.language()))
                    .findFirst()
                    .ifPresent(track -> applyNewDefault(fileInfo, track));
        }
    }

    private Stream<TrackAttributes> getPossibleDefaults(List<TrackAttributes> tracks) {
        Stream<TrackAttributes> attributes = tracks.stream();

        return attributes
                .filter(attr -> !attr.commentary())
                .filter(attr -> {
                    if (attr.trackName() == null) return true;
                    return commentaryKeywords.stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                })
                .filter(attr -> !attr.forced())
                .filter(attr -> {
                    if (attr.trackName() == null) return true;
                    return forcedKeywords.stream().noneMatch(keyword -> keyword.compareToIgnoreCase(attr.trackName()) == 0);
                });
    }

    private void removeExistingDefaults(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(TrackAttributes::defaultt)
                .forEach(attr -> fileInfo.getChanges().getDefaultTrack().put(attr, false));
    }

    private void applyNewDefault(FileInfo fileInfo, TrackAttributes targetDefault) {
        Map<TrackAttributes, Boolean> changes = fileInfo.getChanges().getDefaultTrack();
        if (changes.containsKey(targetDefault)) {
            changes.remove(targetDefault);
        } else {
            changes.put(targetDefault, true);
        }
    }

    public void findAndApplyForcedTracks(FileInfo fileInfo, boolean overwrite) {
        Stream<TrackAttributes> forcedTracks = getForcedTracks(fileInfo);

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

    private Stream<TrackAttributes> getForcedTracks(FileInfo fileInfo) {
        return fileInfo.getTracks().stream()
                .filter(track -> {
                    if (fileInfo.getChanges().getForcedTrack().containsKey(track)) return fileInfo.getChanges().getForcedTrack().get(track);
                    return matchesForcedKeywords(track) || track.forced();
                });
    }

    private boolean matchesForcedKeywords(TrackAttributes track) {
        return track.trackName() != null && forcedKeywords.stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT)));
    }

    public void findAndApplyCommentaryTracks(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(track -> !track.commentary())
                .filter(track -> track.trackName() != null)
                .filter(track -> commentaryKeywords.stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))))
                .forEach(attr -> {
                    fileInfo.getChanges().getCommentaryTrack().put(attr, true);
                });
    }

    public void findAndApplyHearingImpairedTracks(FileInfo fileInfo) {
        fileInfo.getTracks().stream()
                .filter(track -> !track.hearingImpaired())
                .filter(track -> track.trackName() != null)
                .filter(track -> hearingImpairedKeywords.stream().anyMatch(keyword -> track.trackName().toLowerCase().contains(keyword.toLowerCase(Locale.ROOT))))
                .forEach(attr -> {
                    fileInfo.getChanges().getHearingImpairedTrack().put(attr, true);
                });
    }
}

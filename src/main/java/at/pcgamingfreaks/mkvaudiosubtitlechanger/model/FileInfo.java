package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@RequiredArgsConstructor
public class FileInfo {
    private final File file;

    private Set<FileAttribute> existingDefaultAudioLanes = new HashSet<>();
    private Set<FileAttribute> existingForcedAudioLanes = new HashSet<>();

    private Set<FileAttribute> existingDefaultSubtitleLanes = new HashSet<>();
    private Set<FileAttribute> existingForcedSubtitleLanes = new HashSet<>();

    private Set<FileAttribute> desiredForcedSubtitleLanes = new HashSet<>();
    private FileAttribute desiredDefaultAudioLane;
    private FileAttribute desiredDefaultSubtitleLane;
    private AttributeConfig matchedConfig;

    public boolean isAudioDifferent() {
        return isMatchDifferent(existingDefaultAudioLanes, desiredDefaultAudioLane)
                || isLaneOff(existingDefaultAudioLanes, desiredDefaultAudioLane, AttributeConfig::getAudioLanguage);
    }

    public boolean isSubtitleDifferent() {
        return isMatchDifferent(existingDefaultSubtitleLanes, desiredDefaultSubtitleLane)
                || isLaneOff(existingDefaultSubtitleLanes, desiredDefaultSubtitleLane, AttributeConfig::getSubtitleLanguage);
    }

    private boolean isMatchDifferent(Set<FileAttribute> existingDefault, FileAttribute desiredDefault) {
        return desiredDefault != null &&
                (existingDefault == null || !existingDefault.contains(desiredDefault) || existingDefault.size() > 1);
    }

    private boolean isLaneOff(Set<FileAttribute> existingDefault, FileAttribute desiredDefault, Function<AttributeConfig, String> inputLane) {
        return desiredDefault == null
                && (matchedConfig != null && "OFF".equals(inputLane.apply(matchedConfig)))
                && (existingDefault != null && !existingDefault.isEmpty());
    }

    public boolean areForcedTracksDifferent() {
        return !desiredForcedSubtitleLanes.isEmpty() && !existingForcedSubtitleLanes.containsAll(desiredForcedSubtitleLanes);
    }

    public FileStatus getStatus() {
        if (isChangeNecessary()) return FileStatus.CHANGE_NECESSARY;
        if (isUnableToApplyConfig()) return FileStatus.NO_SUITABLE_CONFIG;
        if (isAlreadySuited()) return FileStatus.ALREADY_SUITED;
        return FileStatus.UNKNOWN;
    }

    private boolean isUnableToApplyConfig() {
        return desiredDefaultAudioLane == null && !"OFF".equals(matchedConfig.getAudioLanguage())
                && desiredDefaultSubtitleLane == null && !"OFF".equals(matchedConfig.getSubtitleLanguage());
    }

    private boolean isAlreadySuited() {
        return (desiredDefaultAudioLane == null || existingDefaultAudioLanes.contains(desiredDefaultAudioLane))
                && (desiredDefaultSubtitleLane == null || existingDefaultSubtitleLanes.contains(desiredDefaultSubtitleLane));
    }

    private boolean isChangeNecessary() {
        return isAudioDifferent() || isSubtitleDifferent() || areForcedTracksDifferent() || !existingForcedAudioLanes.isEmpty();
    }

    @Override
    public String toString() {
        return "[" + "defaultAudioLanes=" + existingDefaultAudioLanes +
                ", defaultSubtitleLanes=" + existingDefaultSubtitleLanes +
                ", desiredForcedSubtitleLanes=" + desiredForcedSubtitleLanes +
                ", desiredAudioLane=" + desiredDefaultAudioLane +
                ", desiredSubtitleLane=" + desiredDefaultSubtitleLane +
                ']';
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class FileInfoDto {
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
        return desiredDefaultAudioLane != null &&
                (existingDefaultAudioLanes == null || !existingDefaultAudioLanes.contains(desiredDefaultAudioLane) || existingDefaultAudioLanes.size() > 1);
    }

    public boolean isSubtitleDifferent() {
        return isSubtitleMatchDifferent() || isSubtitleOFF();
    }

    private boolean isSubtitleMatchDifferent() {
        return desiredDefaultSubtitleLane != null
                && (existingDefaultSubtitleLanes == null || !existingDefaultSubtitleLanes.contains(desiredDefaultSubtitleLane) || existingDefaultSubtitleLanes.size() > 1);
    }

    private boolean isSubtitleOFF() {
        return desiredDefaultSubtitleLane == null && "OFF".equals(matchedConfig.getSubtitleLanguage()) &&
                (existingDefaultSubtitleLanes != null && !existingDefaultSubtitleLanes.isEmpty());
    }

    public boolean areForcedTracksDifferent() {
        return !desiredForcedSubtitleLanes.isEmpty();
    }

    public FileStatus getStatus() {
        if (isChangeNecessary()) return FileStatus.CHANGE_NECESSARY;
        if (isUnableToApplyConfig()) return FileStatus.UNABLE_TO_APPLY;
        if (isAlreadySuitable()) return FileStatus.ALREADY_SUITED;
        return FileStatus.UNKNOWN;
    }

    private boolean isUnableToApplyConfig() {
        return desiredDefaultAudioLane == null && desiredDefaultSubtitleLane == null;
    }

    private boolean isAlreadySuitable() {
        return existingDefaultAudioLanes.contains(desiredDefaultAudioLane) && existingDefaultSubtitleLanes.contains(desiredDefaultSubtitleLane);
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

package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class FileInfoDto {
    private Set<FileAttribute> defaultAudioLanes = new HashSet<>();
    private Set<FileAttribute> defaultSubtitleLanes = new HashSet<>();
    private Set<FileAttribute> desiredForcedSubtitleLanes;
    private FileAttribute desiredAudioLane;
    private FileAttribute desiredSubtitleLane;

    public boolean isUnableToApplyConfig() {
        return desiredAudioLane == null && desiredSubtitleLane == null;
    }

    public boolean isAlreadySuitable() {
        return defaultAudioLanes.contains(desiredAudioLane) && defaultSubtitleLanes.contains(desiredSubtitleLane);
    }

    public boolean isChangeNecessary() {
        return isAudioDifferent() || isSubtitleDifferent() || areForcedTracksDifferent();
    }

    public boolean isAudioDifferent() {
        return desiredAudioLane != null &&
                (defaultAudioLanes == null || !defaultAudioLanes.contains(desiredAudioLane));
    }

    public boolean isSubtitleDifferent() {
        return desiredSubtitleLane != null &&
                (defaultSubtitleLanes == null || !defaultSubtitleLanes.contains(desiredSubtitleLane));
    }

    public boolean areForcedTracksDifferent() {
        return desiredForcedSubtitleLanes.size() > 0;
    }

    @Override
    public String toString() {
        return "[" + "defaultAudioLanes=" + defaultAudioLanes +
                ", defaultSubtitleLanes=" + defaultSubtitleLanes +
                ", desiredForcedSubtitleLanes=" + desiredForcedSubtitleLanes +
                ", desiredAudioLane=" + desiredAudioLane +
                ", desiredSubtitleLane=" + desiredSubtitleLane +
                ']';
    }
}

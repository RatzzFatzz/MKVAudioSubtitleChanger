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
    private Set<FileAttribute> defaultAudioLanes = new HashSet<>();
    private Set<FileAttribute> defaultSubtitleLanes = new HashSet<>();
    private Set<FileAttribute> desiredForcedSubtitleLanes;
    private FileAttribute desiredAudioLane;
    private FileAttribute desiredSubtitleLane;

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

    public FileStatus getStatus() {
        if (isChangeNecessary()) return FileStatus.CHANGE_NECESSARY;
        if (isUnableToApplyConfig()) return FileStatus.UNABLE_TO_APPLY;
        if (isAlreadySuitable()) return FileStatus.ALREADY_SUITED;
        return FileStatus.UNKNOWN;
    }

    private boolean isUnableToApplyConfig() {
        return desiredAudioLane == null && desiredSubtitleLane == null;
    }

    private boolean isAlreadySuitable() {
        return defaultAudioLanes.contains(desiredAudioLane) && defaultSubtitleLanes.contains(desiredSubtitleLane);
    }

    private boolean isChangeNecessary() {
        return isAudioDifferent() || isSubtitleDifferent() || areForcedTracksDifferent();
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

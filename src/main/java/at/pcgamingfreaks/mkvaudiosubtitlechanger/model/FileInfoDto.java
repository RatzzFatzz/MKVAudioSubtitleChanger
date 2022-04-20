package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class FileInfoDto {
    private FileAttribute defaultAudioLane;
    private FileAttribute defaultSubtitleLane;
    private Set<FileAttribute> desiredForcedSubtitleLanes;
    private FileAttribute desiredAudioLane;
    private FileAttribute desiredSubtitleLane;

    public boolean isUnableToApplyConfig() {
        return desiredAudioLane == null && desiredSubtitleLane == null;
    }

    public boolean isAlreadySuitable() {
        return desiredAudioLane == defaultAudioLane && desiredSubtitleLane == defaultSubtitleLane;
    }

    public boolean isChangeNecessary() {
        return isAudioDifferent() || isSubtitleDifferent() || areForcedTracksDifferent();
    }

    public boolean isAudioDifferent() {
        return desiredAudioLane != null &&
                (defaultAudioLane == null || defaultAudioLane.getId() != desiredAudioLane.getId());
    }

    public boolean isSubtitleDifferent() {
        return desiredSubtitleLane != null &&
                (defaultSubtitleLane == null || defaultSubtitleLane.getId() != desiredSubtitleLane.getId());
    }

    public boolean areForcedTracksDifferent() {
        return desiredForcedSubtitleLanes.size() > 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("[");
        sb.append("defaultAudioLane=").append(defaultAudioLane);
        sb.append(", defaultSubtitleLane=").append(defaultSubtitleLane);
        sb.append(", desiredForcedSubtitleLanes=").append(desiredForcedSubtitleLanes);
        sb.append(", desiredAudioLane=").append(desiredAudioLane);
        sb.append(", desiredSubtitleLane=").append(desiredSubtitleLane);
        sb.append(']');
        return sb.toString();
    }
}

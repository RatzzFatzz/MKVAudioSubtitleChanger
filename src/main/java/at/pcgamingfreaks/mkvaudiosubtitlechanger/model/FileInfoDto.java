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
}

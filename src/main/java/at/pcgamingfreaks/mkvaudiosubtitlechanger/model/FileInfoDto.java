package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileInfoDto {
    private FileAttribute defaultAudioLane;
    private FileAttribute defaultSubtitleLane;
    private FileAttribute forcedAudioLane;
    private FileAttribute forcedSubtitleLane;
    private FileAttribute desiredAudioLane;
    private FileAttribute desiredSubtitleLane;

    public boolean isChangeNecessary() {
        return defaultAudioLane.getId() != desiredAudioLane.getId()
                || defaultSubtitleLane.getId() != desiredSubtitleLane.getId();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class FileAttribute {
    private final int id;
    private final String language;
    private final String trackName;
    private final boolean defaultTrack;
    private final boolean forcedTrack;
    private final LaneType type;

    public FileAttribute(int id, String language, String trackName, boolean defaultTrack, boolean forcedTrack, LaneType type) {
        this.id = id;
        this.language = language;
        this.trackName = trackName;
        this.defaultTrack = defaultTrack;
        this.forcedTrack = forcedTrack;
        this.type = type;
    }
}

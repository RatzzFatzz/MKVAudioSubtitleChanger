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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("[");
        sb.append("id=").append(id);
        sb.append(", language='").append(language).append('\'');
        sb.append(", trackName='").append(trackName).append('\'');
        sb.append(", defaultTrack=").append(defaultTrack);
        sb.append(", forcedTrack=").append(forcedTrack);
        sb.append(", type=").append(type);
        sb.append(']');
        return sb.toString();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@AllArgsConstructor
public class FileAttribute {
    private final int id;
    private final String language;
    private final String trackName;
    private final boolean defaultTrack;
    private final boolean forcedTrack;
    private final LaneType type;

    @Override
    public String toString() {
        return "[" + "id=" + id +
                ", language='" + language + '\'' +
                ", trackName='" + trackName + '\'' +
                ", defaultTrack=" + defaultTrack +
                ", forcedTrack=" + forcedTrack +
                ", type=" + type +
                ']';
    }
}

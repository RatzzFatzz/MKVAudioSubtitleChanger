package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

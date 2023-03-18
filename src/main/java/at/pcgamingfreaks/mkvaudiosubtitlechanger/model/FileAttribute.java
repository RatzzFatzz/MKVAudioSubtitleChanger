package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileAttribute attribute = (FileAttribute) o;
        return id == attribute.id
                && defaultTrack == attribute.defaultTrack
                && forcedTrack == attribute.forcedTrack
                && Objects.equals(language, attribute.language)
                && Objects.equals(trackName, attribute.trackName)
                && type == attribute.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, trackName, defaultTrack, forcedTrack, type);
    }

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

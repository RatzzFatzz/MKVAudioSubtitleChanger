package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public record FileAttribute(int id, String language, String trackName, boolean defaultTrack, boolean forcedTrack,
                            LaneType type) {
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

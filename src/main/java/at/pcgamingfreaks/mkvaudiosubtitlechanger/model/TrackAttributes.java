package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public record TrackAttributes(int id, String language, String trackName,
                              boolean defaultt, boolean forced, boolean commentary, boolean hearingImpaired,
                              TrackType type) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackAttributes attribute = (TrackAttributes) o;
        return id == attribute.id
                && defaultt == attribute.defaultt
                && forced == attribute.forced
                && Objects.equals(language, attribute.language)
                && Objects.equals(trackName, attribute.trackName)
                && type == attribute.type;
    }

    @Override
    public String toString() {
        return "[" + "id=" + id +
                ", language='" + language + '\'' +
                ", trackName='" + trackName + '\'' +
                ", defaultt=" + defaultt +
                ", forced=" + forced +
                ", type=" + type +
                ']';
    }
}

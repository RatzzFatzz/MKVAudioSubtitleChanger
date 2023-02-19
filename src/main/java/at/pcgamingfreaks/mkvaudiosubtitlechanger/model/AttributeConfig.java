package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Log4j2
@Getter
@AllArgsConstructor
public class AttributeConfig {
    private final String audioLanguage;
    private final String subtitleLanguage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeConfig that = (AttributeConfig) o;
        return Objects.equals(audioLanguage, that.audioLanguage) && Objects.equals(subtitleLanguage, that.subtitleLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(audioLanguage, subtitleLanguage);
    }

    @Override
    public String toString() {
        return "AttributeConfig{"
                + "audioLanguage='" + audioLanguage + '\''
                + ", subtitleLanguage='" + subtitleLanguage + '\'' +
                '}';
    }
}

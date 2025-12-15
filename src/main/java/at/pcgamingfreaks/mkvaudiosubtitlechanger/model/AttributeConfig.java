package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Getter
@AllArgsConstructor
public class AttributeConfig {
    private final String audioLanguage;
    private final String subtitleLanguage;

    public static AttributeConfig of(String audioLanguage, String subtitleLanguage)  {
        return new AttributeConfig(audioLanguage, subtitleLanguage);
    }

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

    public String toStringShort()  {
        return audioLanguage + ":" + subtitleLanguage;
    }

    @Override
    public String toString() {
        return "AttributeConfig{"
                + "audioLanguage='" + audioLanguage + '\''
                + ", subtitleLanguage='" + subtitleLanguage + '\'' +
                '}';
    }
}

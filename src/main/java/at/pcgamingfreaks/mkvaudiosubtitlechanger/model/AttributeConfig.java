package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@AllArgsConstructor
public class AttributeConfig {
    private final String audioLanguage;
    private final String subtitleLanguage;

    @Override
    public String toString() {
        return "AttributeConfig{"
                + "audioLanguage='" + audioLanguage + '\''
                + ", subtitleLanguage='" + subtitleLanguage + '\'' +
                '}';
    }
}

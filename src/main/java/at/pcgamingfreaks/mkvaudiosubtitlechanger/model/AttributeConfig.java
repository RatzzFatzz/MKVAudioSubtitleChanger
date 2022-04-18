package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class AttributeConfig {
    private final String audioLanguage;
    private final String subtitleLanguage;

    public AttributeConfig(String audioLanguage, String subtitleLanguage) {
        this.audioLanguage = audioLanguage;
        this.subtitleLanguage = subtitleLanguage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AttributeConfig{");
        sb.append("audioLanguage='").append(audioLanguage).append('\'');
        sb.append(", subtitleLanguage='").append(subtitleLanguage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

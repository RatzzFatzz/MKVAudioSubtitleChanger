package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class AttributeConfig {
    private String audioLanguage;
    private String subtitleLanguage;

    public AttributeConfig(String audioLanguage, String subtitleLanguage) {
        this.audioLanguage = audioLanguage;
        this.subtitleLanguage = subtitleLanguage;
    }

    public boolean isValid() {
        return audioLanguage != null && subtitleLanguage != null;
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
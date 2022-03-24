package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Getter
public class AttributeConfig {
    private String audio;
    private String subtitle;

    public AttributeConfig(String audio, String subtitle) {
        this.audio = audio;
        this.subtitle = subtitle;
    }

    public boolean isValid() {
        return audio != null && subtitle != null;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AttributeConfig{");
        sb.append("audio='").append(audio).append('\'');
        sb.append(", subtitle='").append(subtitle).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

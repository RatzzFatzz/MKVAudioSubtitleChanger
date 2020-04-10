package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Getter
public class AttributeConfig {
    private List<String> audio;
    private List<String> subtitle;

    public AttributeConfig(List<String> audio, List<String> subtitle) {
        this.audio = audio;
        this.subtitle = subtitle;
    }
}

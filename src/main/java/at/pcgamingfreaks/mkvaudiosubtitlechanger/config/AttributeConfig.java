package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import lombok.Getter;

import java.util.List;

@Getter
public class AttributeConfig {
    private List<String> audio;
    private List<String> subtitle;

    public AttributeConfig(List<String> audio, List<String> subtitle) {
        this.audio = audio;
        this.subtitle = subtitle;
    }
}

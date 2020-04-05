package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import lombok.Getter;

import java.io.File;
import java.util.List;

@Getter
public class AttributeConfig {
    private List<String> audio;
    private List<String> subtitle;

    public AttributeConfig(List<String> audio, List<String> subtitle) {
        this.audio = audio;
        this.subtitle = subtitle;
    }

    /**
     * Processes the config lists and apply the changes if the combination matches
     * @return If the current configuration matched and changes applied or not
     */
    public boolean processConfig(File file, List<FileAttribute> attributes) {
        // check if size is bigger or equal 2 to make sure that there is at least one audio and subtitle line
        // TODO: implement empty audio or subtitle line
        if(attributes.size() >= 2) {
           // TODO: Update queryBuilder:updateAttributes here
        }
        return true;
    }
}

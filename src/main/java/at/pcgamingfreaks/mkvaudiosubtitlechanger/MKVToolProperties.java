package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class MKVToolProperties {
    private String mkvmergePath;
    private String mkvpropeditPath;

    private static MKVToolProperties instance = null;

    private MKVToolProperties() {
    }

    public static MKVToolProperties getInstance() {
        if (instance == null) {
            instance = new MKVToolProperties();
        }
        return instance;
    }
}

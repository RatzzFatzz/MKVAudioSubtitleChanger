package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionUtil {
    public static String getVersion() {
        try (InputStream propertiesStream = VersionUtil.class.getClassLoader().getResourceAsStream("version.properties")) {
            Properties properties = new Properties();
            properties.load(propertiesStream);

            return properties.getProperty("version");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

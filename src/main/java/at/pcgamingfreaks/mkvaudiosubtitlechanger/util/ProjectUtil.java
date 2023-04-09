package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProjectUtil {
    private static final Properties PROJECT_PROPERTIES = new Properties();

    static {
        try (InputStream propertiesStream = ProjectUtil.class.getClassLoader().getResourceAsStream("project.properties")) {
            PROJECT_PROPERTIES.load(propertiesStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getVersion() {
        return PROJECT_PROPERTIES.getProperty("version");
    }

    public static String getProjectName() {
        return  PROJECT_PROPERTIES.getProperty("project_name");
    }
}

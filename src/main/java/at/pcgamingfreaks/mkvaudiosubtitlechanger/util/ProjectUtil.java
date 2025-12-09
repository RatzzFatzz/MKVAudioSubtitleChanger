package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProjectUtil implements CommandLine.IVersionProvider {
    private static final Properties PROJECT_PROPERTIES = new Properties();

    static {
        try (InputStream propertiesStream = ProjectUtil.class.getClassLoader().getResourceAsStream("project.properties")) {
            PROJECT_PROPERTIES.load(propertiesStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // TODO: add mkvmerge and mkvpropedit version
    public String[] getVersion() {
        return new String[] {
                getProjectName() + " " + PROJECT_PROPERTIES.getProperty("version"),
                "Java ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
                "${os.name} ${os.version} ${os.arch}"
        };
    }

    public static String getProjectName() {
        return  PROJECT_PROPERTIES.getProperty("project_name");
    }
}

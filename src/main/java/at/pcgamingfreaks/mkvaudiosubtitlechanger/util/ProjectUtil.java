package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import org.apache.logging.log4j.util.Strings;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public String[] getVersion() throws IOException {
        String mkvpropeeditVersion = getVersion(MkvToolNix.MKV_PROP_EDIT);
        String mkvmergeVersion = getVersion(MkvToolNix.MKV_MERGE);

        return new String[] {
                getProjectName() + " " + PROJECT_PROPERTIES.getProperty("version"),
                "Java ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
                "${os.name} ${os.version} ${os.arch}",
                (!Strings.isBlank(mkvpropeeditVersion) ? mkvpropeeditVersion : "MkvPropEdit not found") + ", " + (!Strings.isBlank(mkvmergeVersion) ? mkvmergeVersion : "MkvMerge not found")
        };
    }

    public static String getProjectName() {
        return  PROJECT_PROPERTIES.getProperty("project_name");
    }

    public static String getVersion(MkvToolNix app) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(app.toString(), "--version");
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String version = reader.readLine();
            int exitCode = process.waitFor();

            if (exitCode == 0) return version;
        } catch (IOException | InterruptedException ignored) {}
        return null;
    }
}

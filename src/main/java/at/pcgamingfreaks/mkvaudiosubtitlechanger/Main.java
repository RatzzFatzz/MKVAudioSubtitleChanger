package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ConfigUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

import static java.lang.Integer.parseInt;

@Log4j2
public class Main {
    public static void main(String[] args) {
        System.out.println(String.join(", ", args));
        initConfig(args);
        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel();
        kernel.execute(ConfigUtil.getInstance().getLibraryPath());
    }

    private static boolean checkIfMKVToolNixIsValid() {
        try {
            String path = new YAML(new File("config.yaml")).getString("mkvtoolnixPath");
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                MKVToolProperties.getInstance().setMkvmergePath(path + "mkvmerge.exe");
                MKVToolProperties.getInstance().setMkvpropeditPath(path + "mkvpropedit.exe");
            } else {
                MKVToolProperties.getInstance().setMkvmergePath(path + "mkvmerge");
                MKVToolProperties.getInstance().setMkvpropeditPath(path + "mkvpropedit");
            }
        } catch (YamlKeyNotFoundException | IOException | YamlInvalidContentException e) {
            e.printStackTrace();
        }
        return new File(MKVToolProperties.getInstance().getMkvmergePath()).isFile() && new File(MKVToolProperties.getInstance().getMkvpropeditPath()).isFile();
    }

    private static void initConfig(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "\"for help this is\" - Yoda");
        options.addRequiredOption("l", "library", true, "path to library");
        options.addOption("c", "config", false, "path to config");
        options.addOption("t", "threads", true, "thread count");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);

            ConfigUtil configUtil = ConfigUtil.getInstance();
            configUtil.loadConfig(cmd.getOptionValue("config", "config.yaml")); // use cmd input
            configUtil.setLibraryPath(cmd.getOptionValue("library"));
            if (cmd.hasOption("threads")) configUtil.setThreadCount(parseInt(cmd.getOptionValue("threads")));
            configUtil.isValid();
        } catch (ParseException e) {
            log.error(e);
            formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
            System.exit(1);
        }
    }
}

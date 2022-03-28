package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import static java.lang.Integer.parseInt;

@Log4j2
public class Main {
    public static void main(String[] args) {
        initConfig(args);
        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel(new MkvFileCollector(), new MkvFileProcessor());
        kernel.execute();
    }

    private static void initConfig(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "\"for help this is\" - Yoda");
        options.addRequiredOption("l", "library", true, "path to library");
        options.addOption("c", "config", false, "path to config");
        options.addOption("t", "threads", true, "thread count");
        options.addOption("s", "safe-mode", false, "Test run (no files will be changes)");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
                System.exit(0);
            }

            Config config = Config.getInstance();
            config.loadConfig(cmd.getOptionValue("config", "config.yaml")); // use cmd input
            config.setLibraryPath(cmd.getOptionValue("library"));
            if (cmd.hasOption("threads")) config.setThreadCount(parseInt(cmd.getOptionValue("threads")));
            config.setSafeMode(cmd.hasOption("safe-mode"));
            config.isValid();
        } catch (ParseException e) {
            log.error(e);
            formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
            System.exit(1);
        }
    }
}

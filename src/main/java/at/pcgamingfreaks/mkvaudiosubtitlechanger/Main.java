package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.util.List;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
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
        options.addOption("h", HELP.toString(), false, "\"for help this is\" - Yoda");
        options.addRequiredOption("l", LIBRARY.toString(), true, "path to library");
        options.addOption("c", CONFIG.toString(), false, "path to config");
        options.addOption("t", THREADS.toString(), true, "thread count");
        options.addOption("s", SAFE_MODE.toString(), false, "Test run (no files will be changes)");
        options.addOption(create("k", FORCED_KEYWORDS.toString(),Option.UNLIMITED_VALUES,"Additional keywords to identify forced tracks"));

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
                System.exit(0);
            }

            Config config = Config.getInstance();
            config.loadConfig(cmd.getOptionValue(CONFIG.toString(), "config.yaml")); // use cmd input
            config.setLibraryPath(cmd.getOptionValue("library"));
            config.setSafeMode(cmd.hasOption("safe-mode"));
            if (cmd.hasOption("threads")) config.setThreadCount(parseInt(cmd.getOptionValue("threads")));
            if (cmd.hasOption(FORCED_KEYWORDS.toString())) config.getForcedKeywords().addAll(List.of(cmd.getOptionValues(FORCED_KEYWORDS.toString())));
            config.isValid();
        } catch (ParseException e) {
            log.error(e);
            formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
            System.exit(1);
        }
    }

    private static Option create(String opt, String longOpt, int args, String desc) {
        Option option = new Option(opt, desc);
        option.setLongOpt(longOpt);
        option.setArgs(args);
        return option;
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.util.List;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static java.lang.Integer.parseInt;

@Log4j2
public class Main {
    public static void main(String[] args) {
        initConfig(args);
        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel(new MkvFileCollector(), new MkvFileProcessor());
        kernel.execute();
    }

    private static void initConfig(String[] args) {
        Options options = initOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
                System.exit(0);
            }

            Config config = Config.getInstance();
            config.loadConfig(cmd.getOptionValue(CONFIG_PATH.prop(), "config.yaml")); // use cmd input
            config.setLibraryPath(cmd.getOptionValue("library"));
            config.setSafeMode(cmd.hasOption("safe-mode"));
            if (cmd.hasOption("threads")) config.setThreadCount(parseInt(cmd.getOptionValue("threads")));
            if (cmd.hasOption(FORCED_KEYWORDS.prop()))
                config.getForcedKeywords().addAll(List.of(cmd.getOptionValues(FORCED_KEYWORDS.prop())));
            if (cmd.hasOption(EXCLUDE_DIRECTORY.prop()))
                config.getExcludedDirectories().addAll(List.of(cmd.getOptionValues(EXCLUDE_DIRECTORY.prop())));
            if (cmd.hasOption(INCLUDE_PATTERN.prop())) {
                config.setIncludePattern(Config.compilePattern(cmd.getOptionValue(INCLUDE_PATTERN.prop()), INCLUDE_PATTERN));
            }
            config.isValid();
        } catch (ParseException e) {
            log.error(e);
            formatter.printHelp("java -jar MKVAudioSubtitlesChanger.jar -p <path_to_library>", options);
            System.exit(1);
        }
    }

    private static Options initOptions() {
        Options options = new Options();
        options.addOption(optionOf(HELP, "h", false));
        options.addOption(optionOf(LIBRARY, "l", true, true));
        options.addOption(optionOf(CONFIG_PATH, "c", false));
        options.addOption(optionOf(THREADS, "t", true));
        options.addOption(optionOf(SAFE_MODE, "s", false));
        options.addOption(optionOf(FORCED_KEYWORDS, "k", Option.UNLIMITED_VALUES, false));
        options.addOption(optionOf(EXCLUDE_DIRECTORY, "e", Option.UNLIMITED_VALUES, false));
        options.addOption(optionOf(INCLUDE_PATTERN, "i", true));
        return options;
    }
}

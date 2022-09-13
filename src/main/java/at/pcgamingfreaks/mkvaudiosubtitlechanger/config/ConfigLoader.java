package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator.*;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.VersionUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;

public class ConfigLoader {
    private static final List<ConfigValidator<?>> VALIDATORS = List.of(
//            new PathValidator(CONFIG_PATH, false, Path.of("./").toFile()), Singelton for yaml instance
            new PathValidator(LIBRARY, true, null),
            new ThreadValidator(THREADS, false, 2),
            new MkvToolNixPathValidator(MKV_TOOL_NIX, true, Path.of("C:\\Program Files\\MKVToolNix").toFile()),
            new BooleanValidator(SAFE_MODE, false),
            new OperatingSystemValidator(WINDOWS),
            new PatternValidator(INCLUDE_PATTERN, false, Pattern.compile(".*")),
            new SetValidator(FORCED_KEYWORDS, false, true),
            new SetValidator(COMMENTARY_KEYWORDS, false, true),
            new SetValidator(EXCLUDE_DIRECTORY, false, true),
            new AttributeConfigValidator()
    );

    public static void initConfig(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        Options options = initOptions();

        try {
            cmd = parser.parse(options, args);
            if (cmd == null) throw new NullPointerException();
        } catch (ParseException | NullPointerException e) {
            formatter.printHelp(106, "java -jar MKVAudioSubtitlesChanger.jar -l <path_to_library>",
                    "\nParameters:", options,
                    "\nFeature requests and bug reports: https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/issues");
            System.exit(1);
        }

        exitIfHelp(cmd, options, formatter);
        exitIfVersion(cmd);

        List<ValidationResult> results = new ArrayList<>();
        try (YAML config = new YAML(loadConfigPath(cmd))) {
            for (ConfigValidator<?> validator : VALIDATORS) {
                results.add(validator.validate(config, cmd));
            }
        } catch (IOException | YamlInvalidContentException ignored) {}

        if (results.contains(ValidationResult.INVALID)) System.exit(1);
    }

    private static Options initOptions() {
        Options options = new Options();
        options.addOption(optionOf(HELP, "h", false));
        options.addOption(optionOf(VERSION, "v", false));
        options.addOption(optionOf(LIBRARY, "l", true));
        options.addOption(optionOf(MKV_TOOL_NIX, "m", true));
        options.addOption(optionOf(CONFIG_PATH, "c", true));
        options.addOption(optionOf(THREADS, "t", true));
        options.addOption(optionOf(SAFE_MODE, "s", false));
        options.addOption(optionOf(FORCED_KEYWORDS, "k", Option.UNLIMITED_VALUES, false));
        options.addOption(optionOf(EXCLUDE_DIRECTORY, "e", Option.UNLIMITED_VALUES, false));
        options.addOption(optionOf(INCLUDE_PATTERN, "i", true));
        return options;
    }

    private static void exitIfHelp(CommandLine cmd, Options options, HelpFormatter formatter) {
        if (cmd.hasOption("help")) {
            formatter.printHelp(106, "java -jar MKVAudioSubtitlesChanger.jar -l <path_to_library>",
                    "\nParameters:", options,
                    "\nFeature requests and bug reports: https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/issues");
            System.exit(0);
        }
    }

    private static void exitIfVersion(CommandLine cmd) {
        if (cmd.hasOption(VERSION.prop())) {
            System.out.printf("MKV Audio Subtitle Changer Version %s%n", VersionUtil.getVersion());
            System.exit(0);
        }
    }

    private static File loadConfigPath(CommandLine cmd) {
        File configPath = new File(cmd.getOptionValue(CONFIG_PATH.prop(), "config.yaml"));
        if (configPath.isFile()) return configPath;

        System.out.println("invalid config path");
        System.exit(1);
        return null;
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator.*;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;

public class ConfigLoader {
    private static final List<ConfigValidator<?>> VALIDATORS = Stream.of(
            new ConfigPathValidator(CONFIG_PATH, false),
            new PathValidator(LIBRARY, true, null),
            new ThreadValidator(THREADS, false, 2),
            new MkvToolNixPathValidator(MKV_TOOL_NIX, true, Path.of("C:\\Program Files\\MKVToolNix").toFile()),
            new BooleanValidator(SAFE_MODE, false),
            new BooleanValidator(ONLY_NEW_FILES, false),
            new DateValidator(FILTER_DATE, false),
            new PatternValidator(INCLUDE_PATTERN, false, Pattern.compile(".*")),
            new SetValidator(FORCED_KEYWORDS, false, true),
            new SetValidator(COMMENTARY_KEYWORDS, false, true),
            new SetValidator(EXCLUDED_DIRECTORY, false, true),
            new SetValidator(PREFERRED_SUBTITLES, false, true),
            new AttributeConfigValidator(),
            new CoherentConfigValidator(COHERENT, false),
            new BooleanValidator(FORCE_COHERENT, false)
    ).sorted(Comparator.comparing((ConfigValidator<?> validator) -> validator.getWeight()).reversed()).collect(Collectors.toList());

    public static void initConfig(String[] args) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        YAML yamlConfig = null;

        Options options = initOptions();
        CommandLine cmd = parseCommandLineArgs(formatter, options, args);

        exitIfHelp(cmd, options, formatter);
        exitIfVersion(cmd);

        List<ValidationResult> results = new ArrayList<>();

        for (ConfigValidator<?> validator: VALIDATORS) {
            results.add(validator.validate(yamlConfig, cmd));
            if (yamlConfig == null) {
                try {
                    yamlConfig = Config.getInstance().getConfigPath() != null
                            ? new YAML(Config.getInstance().getConfigPath())
                            : new YAML("");
                } catch (IOException | YamlInvalidContentException ignored) {}
            }
        }

        if (results.contains(ValidationResult.INVALID) || results.contains(ValidationResult.MISSING)) System.exit(1);
        System.out.println();
    }

    private static Options initOptions() {
        Options options = new Options();
        Arrays.stream(ConfigProperty.values())
                .filter(prop -> prop.abrv() != null)
                .map(prop -> optionOf(prop, prop.abrv(), prop.args()))
                .forEach(options::addOption);
        return options;
    }

    private static CommandLine parseCommandLineArgs(HelpFormatter formatter, Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd == null) throw new NullPointerException();
            return cmd;
        } catch (ParseException | NullPointerException e) {
            formatter.printHelp(130, "java -jar MKVAudioSubtitlesChanger.jar -l <path_to_library>",
                    "\nParameters:", options,
                    "\nFeature requests and bug reports: https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/issues");
            System.exit(1);
        }
        return null; // can't be reached
    }

    private static void exitIfHelp(CommandLine cmd, Options options, HelpFormatter formatter) {
        if (cmd.hasOption(HELP.prop())) {
            formatter.printHelp(130, "java -jar MKVAudioSubtitlesChanger.jar -l <path_to_library>",
                    "\nParameters:", options,
                    "\nFeature requests and bug reports: https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/issues");
            System.exit(0);
        }
    }

    private static void exitIfVersion(CommandLine cmd) {
        if (cmd.hasOption(VERSION.prop())) {
            System.out.printf("MKV Audio Subtitle Changer Version %s%n", ProjectUtil.getVersion());
            System.exit(0);
        }
    }
}

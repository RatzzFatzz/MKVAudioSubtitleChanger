package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.VersionUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.LanguageValidatorUtil.isLanguageValid;

@Log4j2
@Getter
public class Config {
    @Getter(AccessLevel.NONE)
    CommandLineParser parser = new DefaultParser();
    @Getter(AccessLevel.NONE)
    HelpFormatter formatter = new HelpFormatter();

    @Getter(AccessLevel.NONE)
    private static Config config = null;

    private File configPath;
    private String libraryPath;
    private boolean isSafeMode;

    private int threadCount;
    private Pattern includePattern;
    @Getter(AccessLevel.NONE)
    private String mkvToolNixPath;

    private boolean isWindows;

    private final Set<String> forcedKeywords = new HashSet<>(Arrays.asList("forced", "signs"));
    private final Set<String> excludedDirectories = new HashSet<>();

    private List<AttributeConfig> attributeConfig;

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public void initConfig(String[] args) throws InvalidConfigException {
        ConfigErrors errors = new ConfigErrors();
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

        exitIfHelp(cmd, options);
        exitIfVersion(cmd);

        configPath = loadConfigPath(cmd, errors);
        libraryPath = loadLibraryPath(cmd, errors);
        isSafeMode = cmd.hasOption(SAFE_MODE.prop());

        try (YAML config = new YAML(configPath)) {
            threadCount = loadThreadCount(cmd, config);
            includePattern = loadIncludePattern(cmd, config, errors);
            mkvToolNixPath = loadMkvToolNixPath(cmd, config, errors);

            isWindows = loadOperatingSystem();

            loadForcedKeywords(cmd, config);
            loadExcludedDirectories(cmd, config);

            attributeConfig = loadAttributeConfig(config, errors);
        } catch (IOException | YamlInvalidContentException ignored) {}

        if (errors.hasErrors()) {
            throw new InvalidConfigException(errors);
        }
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

    private void exitIfHelp(CommandLine cmd, Options options) {
        if (cmd.hasOption("help")) {
            formatter.printHelp(106, "java -jar MKVAudioSubtitlesChanger.jar -l <path_to_library>",
                    "\nParameters:", options,
                    "\nFeature requests and bug reports: https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/issues");
            System.exit(0);
        }
    }

    private void exitIfVersion(CommandLine cmd) {
        if (cmd.hasOption(VERSION.prop())) {
            System.out.printf("MKV Audio Subtitle Changer Version %s%n", VersionUtil.getVersion());
            System.exit(0);
        }
    }

    private File loadConfigPath(CommandLine cmd, ConfigErrors errors) {
        File configPath = new File(cmd.getOptionValue(CONFIG_PATH.prop(), "config.yaml"));
        if (configPath.isFile()) return configPath;

        errors.add("invalid config path");
        return null;
    }

    private String loadLibraryPath(CommandLine cmd, ConfigErrors errors) {
        if (cmd.hasOption(LIBRARY.prop())) {
            File libraryPath = new File(cmd.getOptionValue(LIBRARY.prop()));
            if (libraryPath.isFile() || libraryPath.isDirectory()) {
                return libraryPath.getAbsolutePath();
            } else {
                errors.add("invalid library path");
            }
        } else {
            errors.add("missing library path");
        }
        return null;
    }

    private int loadThreadCount(CommandLine cmd, YAML config) {
        return cmd.hasOption(THREADS.prop())
                ? Integer.parseInt(cmd.getOptionValue(THREADS.prop()))
                : config.getInt(THREADS.prop(), 2);
    }

    private Pattern loadIncludePattern(CommandLine cmd, YAML config, ConfigErrors errors) {
        try {
            return Pattern.compile(cmd.hasOption(INCLUDE_PATTERN.prop())
                    ? cmd.getOptionValue(INCLUDE_PATTERN.prop())
                    : config.getString(INCLUDE_PATTERN.prop(), ".*"));
        } catch (PatternSyntaxException e) {
            errors.add("invalid regex pattern");
        }
        return null;
    }

    @SneakyThrows
    private String loadMkvToolNixPath(CommandLine cmd, YAML config, ConfigErrors errors){
        if (cmd.hasOption(MKV_TOOL_NIX.prop())) return cmd.getOptionValue(MKV_TOOL_NIX.prop());
        if (config.isSet(MKV_TOOL_NIX.prop())) return config.getString(MKV_TOOL_NIX.prop());
        errors.add("path to mkv tool nix installation missing");
        return null;
    }

    private boolean loadOperatingSystem() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    @SneakyThrows
    private void loadForcedKeywords(CommandLine cmd, YAML config) {
        if (cmd.hasOption(FORCED_KEYWORDS.prop())) forcedKeywords.addAll(List.of(cmd.getOptionValues(FORCED_KEYWORDS.prop())));
        if (config.isSet(FORCED_KEYWORDS.prop())) forcedKeywords.addAll(config.getStringList(FORCED_KEYWORDS.prop()));
    }

    @SneakyThrows
    private void loadExcludedDirectories(CommandLine cmd, YAML config) {
        if (cmd.hasOption(EXCLUDE_DIRECTORY.prop())) excludedDirectories.addAll(List.of(cmd.getOptionValues(EXCLUDE_DIRECTORY.prop())));
        if (config.isSet(EXCLUDE_DIRECTORY.prop())) excludedDirectories.addAll(config.getStringList(EXCLUDE_DIRECTORY.prop()));
    }

    private List<AttributeConfig> loadAttributeConfig(YAML config, ConfigErrors errors) {
        Function<String, String> audio = key -> config.getString(key + ".audio", null);
        Function<String, String> subtitle = key -> config.getString(key + ".subtitle", null);

        List<AttributeConfig> attributeConfigs = config.getKeysFiltered(".*audio.*").stream()
                .sorted()
                .map(key -> key.replace(".audio", ""))
                .map(key -> new AttributeConfig(audio.apply(key), subtitle.apply(key)))
                .collect(Collectors.toList());

        if (attributeConfigs.isEmpty()) {
            errors.add("no language configuration");
        } else {
            for (AttributeConfig attributeConfig : attributeConfigs) {
                isLanguageValid(attributeConfig.getAudioLanguage(), errors);
                isLanguageValid(attributeConfig.getSubtitleLanguage(), errors);
            }
        }

        return attributeConfigs;
    }

    public String getPathFor(MkvToolNix exe) {
        return mkvToolNixPath.endsWith("/") ? mkvToolNixPath + exe : mkvToolNixPath + "/" + exe;
    }
}


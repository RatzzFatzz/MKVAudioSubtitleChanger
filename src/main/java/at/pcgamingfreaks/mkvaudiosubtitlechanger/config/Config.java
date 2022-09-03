package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.VersionUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.LanguageValidatorUtil.isLanguageValid;

@Log4j2
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {
    @Getter(AccessLevel.NONE)
    CommandLineParser parser = new DefaultParser();
    @Getter(AccessLevel.NONE)
    HelpFormatter formatter = new HelpFormatter();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Config config = null;

    private File configPath;
    private File libraryPath;
    @Getter(AccessLevel.NONE)
    private File mkvToolNix;

    private int threads;
    private Pattern includePattern;
    private boolean windows;
    private boolean safeMode;

    private final Set<String> forcedKeywords = new HashSet<>(Arrays.asList("forced", "signs"));
    private final Set<String> commentaryKeywords = new HashSet<>(Arrays.asList("commentary", "director"));
    private final Set<String> excludedDirectories = new HashSet<>();

    private List<AttributeConfig> attributeConfig;

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
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
        return mkvToolNix.getAbsolutePath().endsWith("/") ? mkvToolNix.getAbsolutePath() + exe + ".exe" :
                mkvToolNix.getAbsolutePath() + "/" + exe + ".exe";
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Config.class.getSimpleName() + "[", "]")
                .add("parser=" + parser).add("\n")
                .add("formatter=" + formatter).add("\n")
                .add("configPath=" + configPath).add("\n")
                .add("libraryPath=" + libraryPath).add("\n")
                .add("isWindows=" + windows).add("\n")
                .add("isSafeMode=" + safeMode).add("\n")
                .add("forcedKeywords=" + forcedKeywords).add("\n")
                .add("commentaryKeywords=" + commentaryKeywords).add("\n")
                .add("excludedDirectories=" + excludedDirectories).add("\n")
                .add("threadCount=" + threads).add("\n")
                .add("includePattern=" + includePattern).add("\n")
                .add("mkvToolNixPath='" + mkvToolNix + "'").add("\n")
                .add("attributeConfig=" + attributeConfig)
                .toString();
    }
}


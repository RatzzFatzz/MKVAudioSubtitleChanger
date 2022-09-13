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


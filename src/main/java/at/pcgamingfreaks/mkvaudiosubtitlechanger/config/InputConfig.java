package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.converter.AttributeConfigConverter;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation.ValidFile;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation.ValidMkvToolNix;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileUtils;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import picocli.CommandLine.Option;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@CommandLine.Command
public class InputConfig {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static InputConfig config = null;

    private File configPath;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Option(names = {"-a", "--attribute-config"}, required = true, arity = "1..*", converter = AttributeConfigConverter.class,
            description = "List of audio:subtitle pairs used to match in order and update files accordingly (e.g. jpn:eng jpn:ger)")
    private List<AttributeConfig> attributeConfig;

    @ValidFile(message = "does not exist")
    @Option(names = {"-l", "--library"}, required = true, description = "path to library")
    private File libraryPath;

    @Option(names = {"-s", "--safemode"}, description = "test run (no files will be changes)")
    private boolean safeMode;

    @ValidMkvToolNix(message = "does not exist")
    @Option(names = {"-m", "--mkvtoolnix"}, defaultValue = "${DEFAULT_MKV_TOOL_NIX}", description = "path to mkvtoolnix installation")
    private File mkvToolNix;

    @Min(1)
    @Option(names = {"-t", "--threads"}, defaultValue = "2", description = "thread count (default: ${DEFAULT-VALUE})")
    private int threads;

    @Min(0)
    @Option(names = {"-c", "--coherent"}, description = "try to match all files in dir of depth with the same attribute config")
    private Integer coherent;

    @Option(names = {"-cf", "--force-coherent"}, description = "changes are only applied if it's a coherent match")
    private boolean forceCoherent;

    @Option(names = {"-n", "--only-new-file"}, description = "sets filter-date to last successful execution (overwrites input of filter-date)")
    private boolean onlyNewFiles;

    @Option(names = {"-d", "--filter-date"}, defaultValue = Option.NULL_VALUE, description = "only consider files created newer than entered date (format: \"dd.MM.yyyy-HH:mm:ss\")")
    private Date filterDate;

    @Option(names = {"-i", "--include-pattern"}, defaultValue = ".*", description = "include files matching pattern (default: \".*\")")
    private Pattern includePattern;

    @Option(names = {"-e", "--excluded-directory"}, arity = "1..*",
            description = "Directories to be excluded, combines with config file")
    private Set<String> excludedDirectories = new HashSet<>();

    @Option(names = {"--forced-keywords"}, arity = "1..*", defaultValue = "forced, signs, songs", split = ", ",
            description = "Keywords to identify forced tracks (Defaults will be overwritten; Default: ${DEFAULT-VALUE})")
    private Set<String> forcedKeywords;

    @Option(names = {"--commentary-keywords"}, arity = "1..*", defaultValue = "commentary, director", split = ", ",
            description = "Keywords to identify commentary tracks (Defaults will be overwritten; Default: ${DEFAULT-VALUE})")
    private Set<String> commentaryKeywords;

    @Option(names = {"--preferred-subtitles"}, arity = "1..*", defaultValue = "unstyled", split = ", ",
            description = "Keywords to prefer specific subtitle tracks (Defaults will be overwritten; Default: ${DEFAULT-VALUE})")
    private Set<String> preferredSubtitles;

    @Option(names = {"--debug"}, description = "Enable debug logging")
    private boolean debug;

    static {
        // Set default value into system properties to picocli can read the conditional value
        System.setProperty("DEFAULT_MKV_TOOL_NIX", SystemUtils.IS_OS_WINDOWS ? "C:\\Program Files\\MKVToolNix" : "/usr/bin/");
    }

    public static InputConfig getInstance() {
        return getInstance(false);
    }

    public static InputConfig getInstance(boolean reset) {
        if (config == null || reset) {
            config = new InputConfig();
        }
        return config;
    }

    public static void setInstance(InputConfig c) {
        config = c;
    }

    /**
     * Get path to specific mkvtoolnix application.
     * @return absolute path to desired application.
     */
    public String getPathFor(MkvToolNix application) {
        return FileUtils.getPathFor(mkvToolNix, application).getAbsolutePath();
    }

    public String getNormalizedLibraryPath() {
        return this.getLibraryPath().getAbsolutePath().replace("\\", "/");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", InputConfig.class.getSimpleName() + "[", "]")
                .add("configPath=" + configPath)
                .add("libraryPath=" + libraryPath)
                .add("mkvToolNix=" + mkvToolNix)
                .add("threads=" + threads)
                .add("includePattern=" + includePattern)
                .add("safeMode=" + safeMode)
                .add("coherent=" + coherent)
                .add("forceCoherent=" + forceCoherent)
                .add("onlyNewFiles=" + onlyNewFiles)
                .add("filterDate=" + filterDate)
                .add("forcedKeywords=" + forcedKeywords)
                .add("commentaryKeywords=" + commentaryKeywords)
                .add("excludedDirectories=" + excludedDirectories)
                .add("preferredSubtitles=" + preferredSubtitles)
                .add("attributeConfig=" + attributeConfig)
                .toString();
    }
}


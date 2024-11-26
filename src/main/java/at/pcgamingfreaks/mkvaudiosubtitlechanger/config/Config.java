package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import com.sun.jna.platform.win32.Netapi32Util;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class Config {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Config config = null;

    private File configPath;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-a", "--attribute-config"}, required = true, arity = "1..*", converter = AttributeConfigConverter.class)
    private List<AttributeConfig> attributeConfig;

    @Setter(AccessLevel.NONE)
    private File libraryPath;

    @CommandLine.Option(names = {"-s", "--safemode"}, description = "test run (no files will be changes)")
    private boolean safeMode;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private File mkvToolNix;

    @Min(value = 1)
    @CommandLine.Option(names = {"-t", "--threads"}, defaultValue = "2", description = "thread count (default: ${DEFAULT-VALUE})")
    private int threads;

    @CommandLine.Option(names = {"-c", "--coherent"}, description = "try to match all files in dir of depth with the same attribute config")
    private Integer coherent;
    @CommandLine.Option(names = {"-cf", "--force-coherent"}, description = "changes are only applied if it's a coherent match")
    private boolean forceCoherent;

    @CommandLine.Option(names = {"-n"}, description = "sets filter-date to last successful execution (overwrites input of filter-date)")
    private boolean onlyNewFiles;
    @CommandLine.Option(names = {"-d", "--filter-date"}, defaultValue = CommandLine.Option.NULL_VALUE, description = "only consider files created newer than entered date (format: \"dd.MM.yyyy-HH:mm:ss\")")
    private Date filterDate;
    @CommandLine.Option(names = {"-i", "--include-pattern"}, defaultValue = ".*", description = "include files matching pattern (default: \".*\")")
    private Pattern includePattern;
    @CommandLine.Option(names = {"-e", "--excluded-directory"}, arity = "1..*",
            description = "Directories to be excluded, combines with config file")
    private Set<String> excludedDirectories = new HashSet<>();

    @CommandLine.Option(names = {"-fk", "--force-keywords"}, arity = "1..*",
            description = "Additional keywords to identify forced tracks (Defaults are will be overwritten; Default: ${DEFAULT-VALUE}")
    private Set<String> forcedKeywords = new HashSet<>(Arrays.asList("forced", "signs", "songs"));
    @CommandLine.Option(names = {"-ck", "--commentary-keywords"}, arity = "1..*",
            description = "Additional keywords to identify commentary tracks (Defaults are will be overwritten; Default: ${DEFAULT-VALUE}")
    private Set<String> commentaryKeywords = new HashSet<>(Arrays.asList("commentary", "director"));
    @CommandLine.Option(names = {"-ps", "--preferred-subtiltes"}, arity = "1..*",
            description = "Additional keywords to prefer specific subtitle tracks (Defaults are will be overwritten; Default: ${DEFAULT-VALUE}")
    private Set<String> preferredSubtitles = new HashSet<>(Arrays.asList("unstyled"));


    @CommandLine.Option(names = {"-l", "--library"}, required = true, description = "path to library")
    public void setLibraryPath(File libraryPath) {
        if (!libraryPath.exists()) throw new CommandLine.ParameterException(spec.commandLine(), "Path does not exist: " + libraryPath.getAbsolutePath());
        this.libraryPath = libraryPath;
    }

    @CommandLine.Option(names = {"-m", "--mkvtoolnix"}, defaultValue = "C:\\Program Files\\MKVToolNix", description = "path to mkvtoolnix installation")
    public void setMkvToolNix(File mkvToolNix) {
        this.mkvToolNix = mkvToolNix;
        if (!mkvToolNix.exists()
                || !Path.of(getPathFor(MkvToolNix.MKV_MERGER)).toFile().exists()
                || !Path.of(getPathFor(MkvToolNix.MKV_PROP_EDIT)).toFile().exists()) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    "Invalid path to mkvtoolnix installation: " + mkvToolNix.getAbsolutePath());
        }

    }

    public static Config getInstance() {
        return getInstance(false);
    }

    public static Config getInstance(boolean reset) {
        if (config == null || reset) {
            config = new Config();
        }
        return config;
    }

    public static void setInstance(Config c) {
        config = c;
    }

    /**
     * Get path to specific mkvtoolnix application.
     *
     * @return absolute path to desired application.
     */
    public String getPathFor(MkvToolNix application) {
        return mkvToolNix.getAbsolutePath().endsWith("/") ? mkvToolNix.getAbsolutePath() + application :
                mkvToolNix.getAbsolutePath() + "/" + application;
    }

    public String getNormalizedLibraryPath() {
        return this.getLibraryPath().getAbsolutePath().replace("\\", "/");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Config.class.getSimpleName() + "[", "]")
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


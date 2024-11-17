package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
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
    @Getter(AccessLevel.NONE)
    CommandLineParser parser = new DefaultParser();
    @Getter(AccessLevel.NONE)
    HelpFormatter formatter = new HelpFormatter();

    private File configPath;
    @CommandLine.Option(names = {"-l", "--library"}, required = true, description = "path to library")
    private File libraryPath;
    @Getter(AccessLevel.NONE)
    @CommandLine.Option(names = {"-m", "--mkvtoolnix"}, defaultValue = "C:\\Program Files\\MKVToolNix", description = "path to mkvtoolnix installation")
    private File mkvToolNix;

    @CommandLine.Option(names = {"-t", "--threads"}, defaultValue = "2", description = "thread count (default: 2)")
    private int threads;
    @CommandLine.Option(names = {"-i", "--include-pattern"}, defaultValue = ".*", description = "include files matching pattern (default: \".*\")")
    private Pattern includePattern;
    @CommandLine.Option(names = {"-s", "--safemode"}, description = "test run (no files will be changes)")
    private boolean safeMode;

    @CommandLine.Option(names = {"-c", "--coherent"}, description = "try to match all files in dir of depth with the same attribute config")
    private Integer coherent;
    @CommandLine.Option(names = {"-cf", "--force-coherent"}, description = "changes are only applied if it's a coherent match")
    private boolean forceCoherent;
    @CommandLine.Option(names = {"-n"}, description = "sets filter-date to last successful execution (overwrites input of filter-date)")
    private boolean onlyNewFiles;
    @CommandLine.Option(names = {"-d", "--filter-date"}, defaultValue = CommandLine.Option.NULL_VALUE, description = "only consider files created newer than entered date (format: \"dd.MM.yyyy-HH:mm:ss\")")
    private Date filterDate;

    @CommandLine.Option(names = {"-fk", "--force-keywords"}, description = "Additional keywords to identify forced tracks")
    private Set<String> forcedKeywords = new HashSet<>(Arrays.asList("forced", "signs", "songs"));
    @CommandLine.Option(names = {"-ck", "--commentary-keywords"}, description = "Additional keywords to identify commentary tracks")
    private Set<String> commentaryKeywords = new HashSet<>(Arrays.asList("commentary", "director"));
    @CommandLine.Option(names = {"-e", "--excluded-directory"}, description = "Directories to be excluded, combines with config file")
    private Set<String> excludedDirectories = new HashSet<>();
    @CommandLine.Option(names = {"-ps", "--preferred-subtiltes"}, description = "Additional keywords to prefer specific subtitle tracks")
    private Set<String> preferredSubtitles = new HashSet<>(Arrays.asList("unstyled"));

    @CommandLine.Option(names = {"-a"}, required = true, arity = "1..*", converter = AttributeConfigConverter.class)
    private List<AttributeConfig> attributeConfig;

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
                .add("parser=" + parser)
                .add("formatter=" + formatter)
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


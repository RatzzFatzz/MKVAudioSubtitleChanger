package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.converter.AttributeConfigConverter;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation.ValidFile;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation.ValidMkvToolNix;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import picocli.CommandLine;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import picocli.CommandLine.Option;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@CommandLine.Command
public class InputConfig implements CommandLine.IVersionProvider {

    private File configPath;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @ValidFile(message = "does not exist")
    @CommandLine.Parameters(description = "paths to library", arity = "1..*")
    private File[] libraryPath;

    @Option(names = {"-a", "--attribute-config"}, arity = "1..*", converter = AttributeConfigConverter.class,
            description = "List of audio:subtitle pairs for matching defaults in order (e.g. jpn:eng jpn:ger)")
    private AttributeConfig[] attributeConfig = new AttributeConfig[0];
    @ValidMkvToolNix(message = "does not exist")
    @Option(names = {"-m", "--mkvtoolnix"}, defaultValue = "${DEFAULT_MKV_TOOL_NIX}", description = "path to mkvtoolnix installation")
    private File mkvToolNix;

    @Option(names = {"-s", "--safemode"}, description = "test run (no files will be changes)")
    private boolean safeMode;

    @Min(1)
    @Option(names = {"-t", "--threads"}, defaultValue = "2", showDefaultValue = CommandLine.Help.Visibility.ALWAYS, description = "thread count")
    private int threads;

    @Min(0)
    @Option(names = {"-c", "--coherent"}, description = "try to match all files in dir of depth with the same attribute config. Attempting increasing deeper levels until match is found (worst case applying config on single file basis)")
    private Integer coherent;
    @Option(names = {"-cf", "--force-coherent"}, description = "only applies changes if a coherent match was found for the specifically entered depth")
    private boolean forceCoherent;

    // TODO: implement usage
    @Option(names = {"-n", "--only-new-files"}, description = "ignores all files unchanged and previously processed")
    private boolean onlyNewFiles;
    @Option(names = {"-d", "--filter-date"}, defaultValue = Option.NULL_VALUE, description = "only consider files created newer than entered date (format: \"dd.MM.yyyy-HH:mm:ss\")")
    private Instant filterDate;
    @Option(names = {"-i", "--include-pattern"}, defaultValue = ".*", description = "include files matching pattern")
    private Pattern includePattern;
    @Option(names = {"-e", "--exclude"}, arity = "1..*",
            description = "relative directories and files to be excluded (no wildcard)")
    private Set<String> excluded = new HashSet<>();


    @Option(names =  {"-o", "-overwrite-forced"}, description = "remove all forced flags")
    private boolean overwriteForced;
    @Option(names = {"--forced-keywords"}, arity = "1..*", defaultValue = "forced, signs, songs", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            split = ", ", description = "Keywords to identify forced tracks (Defaults will be overwritten)")
    private Set<String> forcedKeywords;
    @Option(names = {"--commentary-keywords"}, arity = "1..*", defaultValue = "comment, commentary, director", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            split = ", ", description = "Keywords to identify commentary tracks (Defaults will be overwritten)")
    private Set<String> commentaryKeywords;
    @Option(names = {"--hearing-impaired"}, arity = "1..*", defaultValue = "SDH, CC", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            split = ", ", description = "Keywords to identify hearing impaired tracks (Defaults will be overwritten")
    private Set<String> hearingImpaired;
    @Option(names = {"--preferred-subtitles"}, arity = "1..*", defaultValue = "unstyled", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            split = ", ", description = "Keywords to prefer specific subtitle tracks (Defaults will be overwritten)")
    private Set<String> preferredSubtitles;
    @Option(names = {"--debug"}, description = "Enable debug logging")
    private boolean debug;

    static {
        // Set default value into system properties to picocli can read the conditional value
        System.setProperty("DEFAULT_MKV_TOOL_NIX", SystemUtils.IS_OS_WINDOWS ? "C:\\Program Files\\MKVToolNix" : "/usr/bin/");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", InputConfig.class.getSimpleName() + "[", "]")
                .add("configPath=" + configPath)
                .add("spec=" + spec)
                .add("libraryPath=" + libraryPath)
                .add("attributeConfig=" + Arrays.toString(attributeConfig))
                .add("mkvToolNix=" + mkvToolNix)
                .add("safeMode=" + safeMode)
                .add("threads=" + threads)
                .add("coherent=" + coherent)
                .add("forceCoherent=" + forceCoherent)
                .add("filterDate=" + filterDate)
                .add("includePattern=" + includePattern)
                .add("excluded=" + excluded)
                .add("overwriteForced=" + overwriteForced)
                .add("forcedKeywords=" + forcedKeywords)
                .add("commentaryKeywords=" + commentaryKeywords)
                .add("hearingImpaired=" + hearingImpaired)
                .add("preferredSubtitles=" + preferredSubtitles)
                .add("debug=" + debug)
                .toString();
    }

    @Override
    public String[] getVersion() throws Exception {
        return new String[0];
    }
}


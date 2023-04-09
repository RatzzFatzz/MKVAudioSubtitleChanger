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

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Config config = null;
    @Getter(AccessLevel.NONE)
    CommandLineParser parser = new DefaultParser();
    @Getter(AccessLevel.NONE)
    HelpFormatter formatter = new HelpFormatter();
    private File configPath;
    private File libraryPath;
    @Getter(AccessLevel.NONE)
    private File mkvToolNix;

    private int threads;
    private Pattern includePattern;
    private boolean safeMode;

    private Integer coherent;
    private boolean forceCoherent;
    private boolean onlyNewFiles;
    private Date filterDate;

    private Set<String> forcedKeywords = new HashSet<>(Arrays.asList("forced", "signs", "songs"));
    private Set<String> commentaryKeywords = new HashSet<>(Arrays.asList("commentary", "director"));
    private Set<String> excludedDirectories = new HashSet<>();
    private Set<String> preferredSubtitles = new HashSet<>(Arrays.asList("unstyled"));

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


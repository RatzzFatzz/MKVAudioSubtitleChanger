package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;
import org.apache.commons.cli.Option;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum ConfigProperty {
    LIBRARY("library", "Path to library", "l", 1),
    ATTRIBUTE_CONFIG("attribute-config", "Attribute config to decide which tracks to choose when", "a", Option.UNLIMITED_VALUES),
    CONFIG_PATH("config-path", "Path to config file", "p", 1),
    MKV_TOOL_NIX("mkvtoolnix", "Path to mkv tool nix installation", "m", 1),
    SAFE_MODE("safe-mode", "Test run (no files will be changes)", "s", 0),
    COHERENT("coherent", "Try to match whole series with same config", "c", 0),
    WINDOWS("windows", "Is operating system windows", null, 0),
    THREADS("threads", "Thread count (default: 2)", "t", 1),
    INCLUDE_PATTERN("include-pattern", "Include files matching pattern (default: \".*\")", "i", 1),
    EXCLUDE_DIRECTORY("exclude-directories", "Directories to be excluded, combines with config file", "e", 1),
    FORCED_KEYWORDS("forcedKeywords", "Additional keywords to identify forced tracks", "fk", Option.UNLIMITED_VALUES),
    COMMENTARY_KEYWORDS("commentary-keywords", "Additional keywords to identify commentary tracks", "ck", Option.UNLIMITED_VALUES),
    ARGUMENTS("arguments", "List of arguments", null, 0),
    VERSION("version", "Display version", "v", 0),
    HELP("help", "\"For help this is\" - Yoda", "h", 0);

    private final String property;
    private final String description;
    private final String shortParameter;
    private final int args;

    public String prop() {
        return property;
    }

    public String desc() {
        return description;
    }

    public String abrv() {
        return shortParameter;
    }

    public int args() {
        return args;
    }

    /*
     * Verify at startup that there are no duplicated shortParameters.
     */
    static {
        Set<String> shortParameters = new HashSet<>();
        for (String param: Arrays.stream(ConfigProperty.values()).map(ConfigProperty::abrv).collect(Collectors.toList())) {
            if (shortParameters.contains(param)) {
                throw new IllegalStateException("It is not allowed to have multiple properties with the same abbreviation!");
            }
            if (param != null) {
                shortParameters.add(param);
            }
        }
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum ConfigProperty {
    CONFIG_PATH("config-path", "p", "Path to config file"),
    LIBRARY("library", "l", "Path to library"),
    SAFE_MODE("safe-mode", "s", "Test run (no files will be changes)"),
    WINDOWS("windows", null, "Is operating system windows"),
    THREADS("threads", "t", "thread count (default: 2)"),
    INCLUDE_PATTERN("include-pattern", "i", "Include files matching pattern"),
    MKV_TOOL_NIX("mkvtoolnix", "m", "Path to mkv tool nix installation"),
    FORCED_KEYWORDS("forcedKeywords", "fk", "Additional keywords to identify forced tracks"),
    COMMENTARY_KEYWORDS("commentary-keywords", "ck", "Additional keywords to identify commentary tracks"),
    EXCLUDE_DIRECTORY("exclude-directories", "e", "Directories to be excluded, combines with config file"),
    COHERENT("coherent", "c", "Try to match whole series with same config"),
    HELP("help", "h", "\"for help this is\" - Yoda"),
    VERSION("version", "v", "Display version"),
    ARGUMENTS("arguments", null, "List of arguments"),
    ATTRIBUTE_CONFIG("attribute-config", "a", "Attribute config to decide which tracks to choose when");

    private final String property;
    private final String shortParameter;
    private final String description;

    public String prop() {
        return property;
    }

    public String abrv() {
        return shortParameter;
    }

    public String desc() {
        return description;
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

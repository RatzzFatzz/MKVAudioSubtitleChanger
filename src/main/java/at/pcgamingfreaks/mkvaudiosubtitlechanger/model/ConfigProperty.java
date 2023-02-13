package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ConfigProperty {
    CONFIG_PATH("config-path", "c", "Path to config file"),
    LIBRARY("library", "l", "Path to library"),
    SAFE_MODE("safe-mode", "s", "Test run (no files will be changes)"),
    WINDOWS("windows", null, "Is operating system windows"),
    THREADS("threads", "t", "thread count (default: 2)"),
    INCLUDE_PATTERN("include-pattern", "p", "Include files matching pattern"),
    MKV_TOOL_NIX("mkvtoolnix", "m", "Path to mkv tool nix installation"),
    FORCED_KEYWORDS("forcedKeywords", "fk", "Additional keywords to identify forced tracks"),
    COMMENTARY_KEYWORDS("commentary-keywords", "ck", "Additional keywords to identify commentary tracks"),
    EXCLUDE_DIRECTORY("exclude-directories", "e", "Directories to be excluded, combines with config file"),
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
}

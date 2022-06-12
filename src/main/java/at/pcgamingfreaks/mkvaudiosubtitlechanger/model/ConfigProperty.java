package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ConfigProperty {
    CONFIG_PATH("config", "Path to config file"),
    LIBRARY("library", "Path to library"),
    SAFE_MODE("safe-mode", "Test run (no files will be changes)"),
    THREADS("threads", "thread count (default: 2)"),
    INCLUDE_PATTERN("include-pattern", "Include files matching pattern"),
    MKV_TOOL_NIX("mkvtoolnix", "Path to mkv tool nix installation"),
    FORCED_KEYWORDS("forcedKeywords", "Additional keywords to identify forced tracks, combines with config file"),
    EXCLUDE_DIRECTORY("exclude-directories", "Directories to be excluded, combines with config file"),
    HELP("help", "\"for help this is\" - Yoda"),
    VERSION("version", "Display version");

    private final String property;
    private final String description;

    public String desc() {
        return description;
    }

    public String prop() {
        return property;
    }
}

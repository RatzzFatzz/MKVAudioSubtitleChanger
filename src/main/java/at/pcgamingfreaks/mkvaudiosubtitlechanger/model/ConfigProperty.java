package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

public enum ConfigProperty {
    MKV_TOOL_NIX("mkvtoolnixPath", "Path to mkv tool nix installation"),
    THREADS("threads", "thread count"),
    FORCED_KEYWORDS("forcedKeywords", "Additional keywords to identify forced tracks\""),
    CONFIG_PATH("config", "path to config"),
    LIBRARY("library", "path to library"),
    SAFE_MODE("safe-mode", "Test run (no files will be changes)"),
    HELP("help", "\"for help this is\" - Yoda"),
    EXCLUDE_DIRECTORY("exclude-directories", "Directories to exclude");

    private final String property;
    private final String description;

    ConfigProperty(String property, String description) {
        this.property = property;
        this.description = description;
    }

    public String desc() {
        return description;
    }

    public String prop() {
        return property;
    }
}

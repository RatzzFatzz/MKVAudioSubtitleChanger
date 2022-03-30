package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

public enum ConfigProperty {
    MKV_TOOL_NIX("mkvtoolnixPath"),
    THREADS("threads"),
    FORCED_KEYWORDS("forcedKeywords"),
    CONFIG("config"),
    LIBRARY("library"),
    SAFE_MODE("safe-mode"),
    HELP("help");

    private String property;

    ConfigProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return property;
    }
}

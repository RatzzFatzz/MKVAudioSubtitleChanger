package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

public enum LaneType {
    AUDIO,
    SUBTITLES;

    LaneType() {
    }

    @Override
    public String toString() {
        return name();
    }
}

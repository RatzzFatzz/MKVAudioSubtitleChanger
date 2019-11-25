package model;

import lombok.Getter;

@Getter
public class FileAttribute {
    private int id;
    private String language;
    private String trackName;
    private boolean defaultTrack;
    private boolean forcedTrack;
    private String type;

    public FileAttribute(int id, String language, String trackName, boolean defaultTrack, boolean forcedTrack, String type) {
        this.id = id;
        this.language = language;
        this.trackName = trackName;
        this.defaultTrack = defaultTrack;
        this.forcedTrack = forcedTrack;
        this.type = type;
    }
}

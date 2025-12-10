package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileInfo {
    private final File file;

    private final List<TrackAttributes> tracks = new ArrayList<>();

    private final List<TrackAttributes> audioTracks = new ArrayList<>();
    private final List<TrackAttributes> subtitleTracks = new ArrayList<>();

    private final PlannedChange changes = new PlannedChange();
    @Setter
    private AttributeConfig matchedConfig;

    public void addTrack(TrackAttributes track) {
        tracks.add(track);
        if (TrackType.AUDIO.equals(track.type())) audioTracks.add(track);
        else if (TrackType.SUBTITLES.equals(track.type())) subtitleTracks.add(track);
    }

    public void addTracks(Collection<TrackAttributes> tracks) {
        for (TrackAttributes track : tracks) addTrack(track);
    }

    public FileStatus getStatus() {
        if (!changes.isEmpty()) return FileStatus.CHANGE_NECESSARY;
        if (matchedConfig == null) return FileStatus.NO_SUITABLE_CONFIG;
        if (changes.isEmpty()) return FileStatus.ALREADY_SUITED;
        return FileStatus.UNKNOWN;
    }
}

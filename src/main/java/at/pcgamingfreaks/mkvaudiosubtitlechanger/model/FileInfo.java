package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileInfo {
    private final File file;

    @Getter(AccessLevel.NONE)
    private final List<TrackAttributes> tracks = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private final List<TrackAttributes> audioTracks = new ArrayList<>();
    @Getter(AccessLevel.NONE)
    private final List<TrackAttributes> subtitleTracks = new ArrayList<>();

    private PlannedChange changes = new PlannedChange();
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

    public List<TrackAttributes> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public List<TrackAttributes> getAudioTracks() {
        return Collections.unmodifiableList(audioTracks);
    }

    public List<TrackAttributes> getSubtitleTracks() {
        return Collections.unmodifiableList(subtitleTracks);
    }

    public void resetChanges() {
        changes = new PlannedChange();
    }
}

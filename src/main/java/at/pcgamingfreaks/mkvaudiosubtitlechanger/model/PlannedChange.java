package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlannedChange {
    private final Map<TrackAttributes, Boolean> defaultTrack = new HashMap<>();
    private final Map<TrackAttributes, Boolean> forcedTrack = new HashMap<>();
    private final Map<TrackAttributes, Boolean> commentaryTrack = new HashMap<>();
    private final Map<TrackAttributes, Boolean> hearingImpairedTrack = new HashMap<>();

    public boolean isEmpty() {
        return defaultTrack.isEmpty() && forcedTrack.isEmpty() && commentaryTrack.isEmpty() && hearingImpairedTrack.isEmpty();
    }
}

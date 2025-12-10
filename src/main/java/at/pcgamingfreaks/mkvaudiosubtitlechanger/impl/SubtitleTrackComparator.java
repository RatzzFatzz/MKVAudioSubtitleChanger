package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

@RequiredArgsConstructor
public class SubtitleTrackComparator implements Comparator<TrackAttributes> {
    private final String[] preferredSubtitles;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(TrackAttributes track1, TrackAttributes track2) {
        int result = 0;

        if (StringUtils.containsAnyIgnoreCase(track1.trackName(), preferredSubtitles)) {
            result++;
        }
        if (StringUtils.containsAnyIgnoreCase(track2.trackName(), preferredSubtitles)) {
            result--;
        }

        if (result == 0) {
            if (track1.defaultt()) result++;
            if (track2.defaultt()) result--;
        }

        return result;
    }
}

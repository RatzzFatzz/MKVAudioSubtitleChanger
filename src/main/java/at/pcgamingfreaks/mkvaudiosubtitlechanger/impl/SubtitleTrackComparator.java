package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

@RequiredArgsConstructor
public class SubtitleTrackComparator implements Comparator<FileAttribute> {
    private final String[] preferredSubtitles;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(FileAttribute track1, FileAttribute track2) {
        int result = 0;

        if (StringUtils.containsAnyIgnoreCase(track1.trackName(), preferredSubtitles)) {
            result++;
        }
        if (StringUtils.containsAnyIgnoreCase(track2.trackName(), preferredSubtitles)) {
            result--;
        }

        if (result == 0) {
            if (track1.defaultTrack()) result++;
            if (track2.defaultTrack()) result--;
        }

        return result;
    }
}

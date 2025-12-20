package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class SubtitleTrackComparator implements Comparator<TrackAttributes> {
    private final Set<String> preferredSubtitles;
    private final Set<String> hearingImpairedKeywords;

    public SubtitleTrackComparator(Collection<String> preferredSubtitles, Collection<String> hearingImpairedKeywords) {
        this.preferredSubtitles = new HashSet<>(preferredSubtitles.stream().map(String::toLowerCase).toList());
        this.hearingImpairedKeywords = new HashSet<>(hearingImpairedKeywords.stream().map(String::toLowerCase).toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(TrackAttributes track1, TrackAttributes track2) {
        int result = 0;

        String track1Name = track1.trackName().toLowerCase();
        String track2Name = track2.trackName().toLowerCase();

        if (preferredSubtitles.contains(track1Name)) result++;
        else for (String keyword: preferredSubtitles) if (track1Name.contains(keyword)) result++;

        if (preferredSubtitles.contains(track2Name)) result--;
        else for (String keyword: preferredSubtitles) if (track2Name.contains(keyword)) result--;


        if (track1.hearingImpaired()) result--;
        else if (hearingImpairedKeywords.contains(track1Name)) result--;
        else for (String keyword: hearingImpairedKeywords) if (track1Name.contains(keyword)) result--;
        if (track2.hearingImpaired()) result++;
        else if (hearingImpairedKeywords.contains(track2Name)) result++;
        else for (String keyword: hearingImpairedKeywords) if (track2Name.contains(keyword)) result++;

        if (result == 0) {
            if (track1.defaultt()) result++;
            if (track2.defaultt()) result--;
        }

        return result;
    }
}

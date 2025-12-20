package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubtitleTrackComparatorTest {
    private static Stream<Arguments> compareArguments() {
        return Stream.of(
                Arguments.of(attr(""), attr(""), 0),
                Arguments.of(attr("pref"), attr(""), 1),
                Arguments.of(attr(""), attr("pref"), -1),
                Arguments.of(attr("pref"), attr("pref"), 0),

                Arguments.of(attr("", true), attr("", true), 0),
                Arguments.of(attr("", true), attr(""), -1),
                Arguments.of(attr("CC", true), attr(""), -1),
                Arguments.of(attr("CC"), attr(""), -1),
                Arguments.of(attr(""), attr("", true), 1),
                Arguments.of(attr(""), attr("CC", true), 1),
                Arguments.of(attr(""), attr("CC"), 1),

                Arguments.of(attr("pref", true), attr("pref"), -1),
                Arguments.of(attr("pref", true), attr("pref", true), 0),
                Arguments.of(attr("pref"), attr("pref", true), 1),
                Arguments.of(attr("", true), attr("pref"), -2),
                Arguments.of(attr("pref"), attr("", true), 2)
        );
    }

    @ParameterizedTest
    @MethodSource("compareArguments")
    void compare(TrackAttributes track1, TrackAttributes track2, int expected) {
        SubtitleTrackComparator comparator = new SubtitleTrackComparator(List.of("pref"), List.of("CC", "SDH"));
        int actual = comparator.compare(track1, track2);
        assertEquals(expected, actual);
    }

    private static TrackAttributes attr(String trackname) {
        return attr(trackname, false);
    }

    private static TrackAttributes attr(String trackName, boolean hearingImpaired) {
        return new TrackAttributes(0, "", trackName, false, false, false, hearingImpaired, TrackType.SUBTITLES);
    }
}
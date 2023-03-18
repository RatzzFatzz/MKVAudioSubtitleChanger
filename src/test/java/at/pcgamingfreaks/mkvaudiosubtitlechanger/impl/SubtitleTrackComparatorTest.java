package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubtitleTrackComparatorTest {
    private static final SubtitleTrackComparator comparator = new SubtitleTrackComparator(new String[]{"unstyled"});

    private static Stream<Arguments> compareArguments() {
        return Stream.of(
                Arguments.of(List.of(attr("unstyled sub", false), attr("styled sub", false)),
                        List.of(attr("unstyled sub", false), attr("styled sub", false))),
                Arguments.of(List.of(attr("styled sub", false), attr("unstyled sub", false)),
                        List.of(attr("unstyled sub", false), attr("styled sub", false))),

                Arguments.of(List.of(attr("unstyled sub", true), attr("styled sub", false)),
                        List.of(attr("unstyled sub", true), attr("styled sub", false))),
                Arguments.of(List.of(attr("styled sub", true), attr("unstyled sub", false)),
                        List.of(attr("unstyled sub", false), attr("styled sub", true))),

                Arguments.of(List.of(attr("unstyled sub", true), attr("unstyled sub", false)),
                        List.of(attr("unstyled sub", true), attr("unstyled sub", false)))
        );
    }

    @ParameterizedTest
    @MethodSource("compareArguments")
    void compare(List<FileAttribute> input, List<FileAttribute> expected) {
        List<FileAttribute> result = input.stream().sorted(comparator.reversed()).collect(Collectors.toList());

        assertArrayEquals(expected.toArray(new FileAttribute[0]), result.toArray(new FileAttribute[0]));
    }

    private static FileAttribute attr(String trackName, boolean defaultTrack) {
        return new FileAttribute(0, "", trackName, defaultTrack, false, LaneType.SUBTITLES);
    }
}
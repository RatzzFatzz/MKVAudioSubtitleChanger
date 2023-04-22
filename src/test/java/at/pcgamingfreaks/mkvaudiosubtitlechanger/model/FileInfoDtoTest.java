package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.createFileInfo;
import static org.junit.jupiter.api.Assertions.*;

class FileInfoDtoTest {
    private static final FileAttribute AUDIO_GER_DEFAULT = new FileAttribute(0, "ger", "", true, false, LaneType.AUDIO);
    private static final FileAttribute AUDIO_GER = new FileAttribute(0, "ger", "", false, false, LaneType.AUDIO);
    private static final FileAttribute AUDIO_ENG_DEFAULT = new FileAttribute(1, "eng", "", true, false, LaneType.AUDIO);
    private static final FileAttribute AUDIO_ENG = new FileAttribute(1, "eng", "", false, false, LaneType.AUDIO);

    private static final FileAttribute SUB_GER_DEFAULT = new FileAttribute(0, "ger", "", true, false, LaneType.SUBTITLES);
    private static final FileAttribute SUB_GER = new FileAttribute(0, "ger", "", false, false, LaneType.SUBTITLES);
    private static final FileAttribute SUB_ENG_DEFAULT = new FileAttribute(1, "eng", "", true, false, LaneType.SUBTITLES);
    private static final FileAttribute SUB_ENG = new FileAttribute(1, "eng", "", false, false, LaneType.SUBTITLES);


    private static Stream<Arguments> isAudioDifferent() {
        return Stream.of(
                Arguments.of(createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT), false),
                Arguments.of(createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_ENG), true),
                Arguments.of(createFileInfo(Set.of(AUDIO_GER_DEFAULT, AUDIO_ENG_DEFAULT), AUDIO_GER_DEFAULT), true),
                Arguments.of(createFileInfo(Set.of(), AUDIO_GER), true),
                Arguments.of(createFileInfo(null, AUDIO_GER), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void isAudioDifferent(FileInfoDto underTest, boolean expected) {
        assertEquals(expected, underTest.isAudioDifferent());
    }

    private static Stream<Arguments> isSubtitleDifferent() {
        return Stream.of(
                Arguments.of(createFileInfo(Set.of(SUB_GER_DEFAULT), SUB_GER_DEFAULT, new AttributeConfig("", "ger")), false),
                Arguments.of(createFileInfo(Set.of(SUB_GER_DEFAULT), SUB_ENG, new AttributeConfig("", "eng")), true),
                Arguments.of(createFileInfo(Set.of(SUB_GER_DEFAULT, SUB_ENG_DEFAULT), SUB_ENG, new AttributeConfig("", "eng")), true),
                Arguments.of(createFileInfo(Set.of(), SUB_ENG, new AttributeConfig("", "ger")), true),
                Arguments.of(createFileInfo(null, SUB_GER, new AttributeConfig("", "ger")), true),
                Arguments.of(createFileInfo(null, null, new AttributeConfig("", "OFF")), false),
                Arguments.of(createFileInfo(Set.of(), null, new AttributeConfig("", "OFF")), false),
                Arguments.of(createFileInfo(Set.of(SUB_GER_DEFAULT), null, new AttributeConfig("", "OFF")), true)
            );
    }

    @ParameterizedTest
    @MethodSource
    void isSubtitleDifferent(FileInfoDto underTest, boolean expected) {
        assertEquals(expected, underTest.isSubtitleDifferent());
    }
}
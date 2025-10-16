package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileStatus.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileInfoTestUtil.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class FileInfoTest {

    private static Stream<Arguments> isAudioDifferent() {
        return Stream.of(
                Arguments.of(createFileInfoAudio(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT, new AttributeConfig("ger", "")), false),
                Arguments.of(createFileInfoAudio(Set.of(AUDIO_GER_DEFAULT), AUDIO_ENG, new AttributeConfig("eng", "")), true),
                Arguments.of(createFileInfoAudio(Set.of(AUDIO_GER_DEFAULT, AUDIO_ENG_DEFAULT), AUDIO_GER_DEFAULT, new AttributeConfig("ger", "")), true),
                Arguments.of(createFileInfoAudio(Set.of(), AUDIO_GER, new AttributeConfig("ger", "")), true),
                Arguments.of(createFileInfoAudio(null, AUDIO_GER, new AttributeConfig("ger", "")), true),

                Arguments.of(createFileInfoAudio(Set.of(AUDIO_GER_DEFAULT), null, new AttributeConfig("OFF", "")), true),
                Arguments.of(createFileInfoAudio(Set.of(), null, new AttributeConfig("OFF", "")), false),
                Arguments.of(createFileInfoAudio(null, null, new AttributeConfig("OFF", "")), false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void isAudioDifferent(FileInfo underTest, boolean expected) {
        assertEquals(expected, underTest.isAudioDifferent());
    }

    private static Stream<Arguments> isSubtitleDifferent() {
        return Stream.of(
                Arguments.of(createFileInfoSubs(Set.of(SUB_GER_DEFAULT), SUB_GER_DEFAULT, new AttributeConfig("", "ger")), false),
                Arguments.of(createFileInfoSubs(Set.of(SUB_GER_DEFAULT), SUB_ENG, new AttributeConfig("", "eng")), true),
                Arguments.of(createFileInfoSubs(Set.of(SUB_GER_DEFAULT, SUB_ENG_DEFAULT), SUB_ENG, new AttributeConfig("", "eng")), true),
                Arguments.of(createFileInfoSubs(Set.of(), SUB_ENG, new AttributeConfig("", "ger")), true),
                Arguments.of(createFileInfoSubs(null, SUB_GER, new AttributeConfig("", "ger")), true),
                Arguments.of(createFileInfoSubs(null, null, new AttributeConfig("", "OFF")), false),
                Arguments.of(createFileInfoSubs(Set.of(), null, new AttributeConfig("", "OFF")), false),
                Arguments.of(createFileInfoSubs(Set.of(SUB_GER_DEFAULT), null, new AttributeConfig("", "OFF")), true)
            );
    }

    @ParameterizedTest
    @MethodSource
    void isSubtitleDifferent(FileInfo underTest, boolean expected) {
        assertEquals(expected, underTest.isSubtitleDifferent());
    }

    private static Stream<Arguments> getStatus() {
        return Stream.of(
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_ENG, Set.of(), null, Set.of(), Set.of(), Set.of(), new AttributeConfig("eng", "OFF"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(), null, Set.of(SUB_GER_DEFAULT), SUB_ENG, Set.of(), Set.of(), Set.of(), new AttributeConfig("OFF", "eng"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_ENG, Set.of(SUB_GER_DEFAULT), SUB_ENG, Set.of(), Set.of(), Set.of(), new AttributeConfig("OFF", "eng"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT, Set.of(SUB_GER_DEFAULT), SUB_GER_DEFAULT, Set.of(AUDIO_ENG_FORCED), Set.of(), Set.of(), new AttributeConfig("ger", "ger"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(), null, Set.of(), null, Set.of(AUDIO_ENG_FORCED), Set.of(), Set.of(), new AttributeConfig("OFF", "OFF"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(), null, Set.of(), null, Set.of(), Set.of(), Set.of(SUB_GER_FORCED), new AttributeConfig("OFF", "OFF"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(), null, Set.of(), null, Set.of(), Set.of(SUB_ENG_FORCED), Set.of(SUB_GER_FORCED), new AttributeConfig("OFF", "OFF"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(), null, Set.of(), null, Set.of(AUDIO_ENG_FORCED), Set.of(SUB_ENG_FORCED), Set.of(SUB_GER_FORCED), new AttributeConfig("OFF", "OFF"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_ENG, Set.of(SUB_GER_DEFAULT), SUB_ENG, Set.of(AUDIO_ENG_FORCED), Set.of(SUB_ENG_FORCED), Set.of(SUB_GER_FORCED), new AttributeConfig("eng", "eng"))),
                Arguments.of(CHANGE_NECESSARY, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT, Set.of(SUB_GER_DEFAULT), SUB_GER_DEFAULT, Set.of(), Set.of(SUB_ENG_FORCED), Set.of(SUB_ENG_FORCED, SUB_GER), new AttributeConfig("ger", "ger"))),

                Arguments.of(NO_SUITABLE_CONFIG, createFileInfo(Set.of(AUDIO_ENG_DEFAULT), null, Set.of(SUB_GER_DEFAULT), null, Set.of(), Set.of(), Set.of(), new AttributeConfig("eng", "ger"))),
                Arguments.of(NO_SUITABLE_CONFIG, createFileInfo(Set.of(AUDIO_ENG_DEFAULT), null, Set.of(), null, Set.of(), Set.of(), Set.of(), new AttributeConfig("eng", "ger"))),
                Arguments.of(NO_SUITABLE_CONFIG, createFileInfo(Set.of(), null, Set.of(), null, Set.of(), Set.of(), Set.of(), new AttributeConfig("eng", "ger"))),

                Arguments.of(ALREADY_SUITED, createFileInfo(Set.of(), null, Set.of(), null, Set.of(), Set.of(), Set.of(), new AttributeConfig("OFF", "OFF"))),
                Arguments.of(ALREADY_SUITED, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT, Set.of(), null, Set.of(), Set.of(), Set.of(), new AttributeConfig("ger", "OFF"))),
                Arguments.of(ALREADY_SUITED, createFileInfo(Set.of(), null, Set.of(SUB_ENG_DEFAULT), SUB_ENG_DEFAULT, Set.of(), Set.of(), Set.of(), new AttributeConfig("OFF", "ger"))),
                Arguments.of(ALREADY_SUITED, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT, Set.of(SUB_ENG_DEFAULT), SUB_ENG_DEFAULT, Set.of(), Set.of(), Set.of(), new AttributeConfig("ger", "eng"))),
                Arguments.of(ALREADY_SUITED, createFileInfo(Set.of(AUDIO_GER_DEFAULT), AUDIO_GER_DEFAULT, Set.of(SUB_ENG_DEFAULT), SUB_ENG_DEFAULT, Set.of(), Set.of(SUB_GER_FORCED), Set.of(SUB_GER_FORCED), new AttributeConfig("ger", "eng")))
        );
    }

    @ParameterizedTest
    @MethodSource
    void getStatus(FileStatus expected, FileInfo underTest) {
        FileStatus actual = underTest.getStatus();
        assertEquals(expected, actual);
    }
}
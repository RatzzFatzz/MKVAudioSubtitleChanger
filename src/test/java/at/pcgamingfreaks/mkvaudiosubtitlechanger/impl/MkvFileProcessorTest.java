package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileInfoTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class MkvFileProcessorTest {

    private static Stream<Arguments> detectDesiredTracks() {
        return Stream.of(
                Arguments.of(new AttributeConfig("ger", "OFF"), List.of(AUDIO_GER, AUDIO_ENG), new AttributeConfig[] {new AttributeConfig("ger", "OFF"), new AttributeConfig("eng", "OFF")}),
                Arguments.of(new AttributeConfig("eng", "OFF"), List.of(AUDIO_ENG), new AttributeConfig[] {new AttributeConfig("ger", "OFF"), new AttributeConfig("eng", "OFF")}),
                Arguments.of(new AttributeConfig("eng", "ger"), List.of(AUDIO_GER, AUDIO_ENG, SUB_GER, SUB_ENG), new AttributeConfig[] {new AttributeConfig("eng", "ger"), new AttributeConfig("ger", "eng")}),
                Arguments.of(new AttributeConfig("ger", "eng"), List.of(AUDIO_GER, SUB_GER, SUB_ENG), new AttributeConfig[] {new AttributeConfig("eng", "ger"), new AttributeConfig("ger", "eng")}),
                Arguments.of(new AttributeConfig("OFF", "ger"), List.of(AUDIO_GER, SUB_GER, SUB_ENG), new AttributeConfig[] {new AttributeConfig("OFF", "ger"), new AttributeConfig("ger", "eng")})
        );
    }

    @ParameterizedTest
    @MethodSource
    void detectDesiredTracks(AttributeConfig expectedMatch, List<FileAttribute> tracks, AttributeConfig... configs) {
        Config.getInstance().setPreferredSubtitles(Set.of());
        FileInfo info = new FileInfo(null);
        MkvFileProcessor processor = new MkvFileProcessor();
        processor.detectDesiredTracks(info, tracks, tracks, configs);
        assertEquals(expectedMatch.getAudioLanguage(), info.getMatchedConfig().getAudioLanguage());
        assertEquals(expectedMatch.getSubtitleLanguage(), info.getMatchedConfig().getSubtitleLanguage());
    }

    private static Stream<Arguments> retrieveNonForcedTracks() {
        return Stream.of(
                Arguments.of(List.of(SUB_GER, SUB_ENG, SUB_GER_FORCED), List.of(SUB_GER, SUB_ENG)),
                Arguments.of(List.of(SUB_GER, SUB_ENG), List.of(SUB_GER, SUB_ENG)),
                Arguments.of(List.of(AUDIO_GER, SUB_GER, SUB_ENG), List.of(AUDIO_GER, SUB_GER, SUB_ENG)),
                Arguments.of(List.of(AUDIO_GER), List.of(AUDIO_GER)),
                Arguments.of(List.of(AUDIO_GER_FORCED), List.of()),
                Arguments.of(List.of(), List.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void retrieveNonForcedTracks(List<FileAttribute> attributes, List<FileAttribute> expected) {
        Config.getInstance().setPreferredSubtitles(Set.of());
        Config.getInstance().setForcedKeywords(Set.of("forced"));
        MkvFileProcessor processor = new MkvFileProcessor();
        List<FileAttribute> actual = processor.retrieveNonForcedTracks(attributes);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private static Stream<Arguments> retrieveNonCommentaryTracks() {
        return Stream.of(
                Arguments.of(List.of(SUB_GER, SUB_ENG, AUDIO_GER_COMMENTARY), List.of(SUB_GER, SUB_ENG)),
                Arguments.of(List.of(SUB_GER, SUB_ENG), List.of(SUB_GER, SUB_ENG)),
                Arguments.of(List.of(AUDIO_GER, SUB_GER, SUB_ENG), List.of(AUDIO_GER, SUB_GER, SUB_ENG)),
                Arguments.of(List.of(AUDIO_GER), List.of(AUDIO_GER)),
                Arguments.of(List.of(AUDIO_GER_COMMENTARY), List.of()),
                Arguments.of(List.of(), List.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void retrieveNonCommentaryTracks(List<FileAttribute> attributes, List<FileAttribute> expected) {
        Config.getInstance().setPreferredSubtitles(Set.of());
        Config.getInstance().setCommentaryKeywords(Set.of("commentary"));
        MkvFileProcessor processor = new MkvFileProcessor();
        List<FileAttribute> actual = processor.retrieveNonCommentaryTracks(attributes);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
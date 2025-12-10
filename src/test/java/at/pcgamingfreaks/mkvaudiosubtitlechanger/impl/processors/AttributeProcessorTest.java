package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileInfoTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributeProcessorTest {

    private static Stream<Arguments> attributeConfigMatching() {
        return Stream.of(
                Arguments.of(
                        List.of(withName(AUDIO_ENG, null), SUB_ENG),
                        arr(a("eng:eng")), "eng:eng",
                        Map.ofEntries(on(withName(AUDIO_ENG, null)), on(SUB_ENG))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG, SUB_ENG),
                        arr(a("eng:eng")), "eng:eng",
                        Map.ofEntries(on(AUDIO_ENG), on(SUB_ENG))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG, AUDIO_GER, SUB_ENG, SUB_GER),
                        arr(a("eng:eng")), "eng:eng",
                        Map.ofEntries(on(AUDIO_ENG), on(SUB_ENG))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG_DEFAULT, AUDIO_GER, SUB_ENG, SUB_GER),
                        arr(a("ger:eng")), "ger:eng",
                        Map.ofEntries(off(AUDIO_ENG_DEFAULT), on(AUDIO_GER), on(SUB_ENG))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG_DEFAULT, AUDIO_GER, SUB_ENG, SUB_GER),
                        arr(a("eng:ger")), "eng:ger",
                        Map.ofEntries(on(SUB_GER))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG_DEFAULT, AUDIO_GER, SUB_ENG_DEFAULT, SUB_GER),
                        arr(a("eng:OFF")), "eng:OFF",
                        Map.ofEntries(off(SUB_ENG_DEFAULT))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG_DEFAULT, AUDIO_GER, SUB_ENG_DEFAULT, SUB_GER),
                        arr(a("OFF:OFF")), "OFF:OFF",
                        Map.ofEntries(off(AUDIO_ENG_DEFAULT), off(SUB_ENG_DEFAULT))
                ),
                Arguments.of(
                        List.of(AUDIO_ENG_DEFAULT, AUDIO_GER, SUB_GER),
                        arr(a("eng:eng"), a("eng:ger")), "eng:ger",
                        Map.ofEntries(on(SUB_GER))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("attributeConfigMatching")
    void findDefaultMatchAndApplyChanges(List<TrackAttributes> tracks, AttributeConfig[] config, String expectedConfig, Map<TrackAttributes, Boolean> changes) {
        InputConfig.getInstance().setPreferredSubtitles(new HashSet<>());
        InputConfig.getInstance().setCommentaryKeywords(new HashSet<>());
        InputConfig.getInstance().setForcedKeywords(new HashSet<>());

        FileInfo fileInfo = new FileInfo(null);
        fileInfo.addTracks(tracks);
        AttributeProcessor.findDefaultMatchAndApplyChanges(fileInfo, config);
        assertEquals(Strings.isBlank(expectedConfig), fileInfo.getMatchedConfig() == null);
        assertEquals(expectedConfig, fileInfo.getMatchedConfig().toStringShort());
        assertEquals(changes.size(), fileInfo.getChanges().getDefaultTrack().size());
        changes.forEach((key, value) -> {
            assertTrue(fileInfo.getChanges().getDefaultTrack().containsKey(key));
            assertEquals(value, fileInfo.getChanges().getDefaultTrack().get(key));
        });
    }

    private static AttributeConfig[] arr(AttributeConfig... configs) {
        return configs;
    }

    private static AttributeConfig a(String config) {
        String[] split = config.split(":");
        return new AttributeConfig(split[0], split[1]);
    }

    private static Map.Entry<TrackAttributes, Boolean> on(TrackAttributes track) {
        return Map.entry(track, true);
    }

    private static Map.Entry<TrackAttributes, Boolean> off(TrackAttributes track) {
        return Map.entry(track, false);
    }

    private static Stream<Arguments> filterForPossibleDefaults() {
        return Stream.of(

        );
    }

    @ParameterizedTest
    @MethodSource("filterForPossibleDefaults")
    void filterForPossibleDefaults(List<TrackAttributes> tracks, Set<TrackAttributes> expected) {

    }

    private static Stream<Arguments> findForcedTracksAndApplyChanges() {
        return Stream.of(
                Arguments.of(List.of(withName(SUB_GER, "song & signs"), withName(SUB_GER, null)),
                        Set.of("song & signs"), false,
                        Map.ofEntries(on(withName(SUB_GER, "song & signs")))
                ),
                Arguments.of(List.of(withName(SUB_GER, "song & signs")),
                        Set.of("song & signs"), false,
                        Map.ofEntries(on(withName(SUB_GER, "song & signs")))
                ),
                Arguments.of(List.of(withName(SUB_GER_FORCED, "song & signs")),
                        Set.of("song & signs"), false,
                        Map.ofEntries()
                ),
                Arguments.of(List.of(SUB_GER_FORCED, withName(SUB_GER, "song & signs")),
                        Set.of("song & signs"), true,
                        Map.ofEntries(off(SUB_GER_FORCED), on(withName(SUB_GER, "song & signs")))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("findForcedTracksAndApplyChanges")
    void findForcedTracksAndApplyChanges(List<TrackAttributes> tracks, Set<String> keywords, boolean overwrite, Map<TrackAttributes, Boolean> changes) {
        InputConfig.getInstance().setPreferredSubtitles(new HashSet<>());
        InputConfig.getInstance().setCommentaryKeywords(new HashSet<>());
        InputConfig.getInstance().setHearingImpaired(new HashSet<>());
        InputConfig.getInstance().setForcedKeywords(keywords);
        InputConfig.getInstance().setOverwriteForced(overwrite);

        FileInfo fileInfo = new FileInfo(null);
        fileInfo.addTracks(tracks);
        AttributeProcessor.findForcedTracksAndApplyChanges(fileInfo);

        assertEquals(changes.size(), fileInfo.getChanges().getForcedTrack().size());
        changes.forEach((key, value) -> {
            assertTrue(fileInfo.getChanges().getForcedTrack().containsKey(key));
            assertEquals(value, fileInfo.getChanges().getForcedTrack().get(key));
        });
    }

    private static Stream<Arguments> findCommentaryTracksAndApplyChanges() {
        return Stream.of(
                Arguments.of(List.of(withName(SUB_GER, "commentary"), withName(SUB_GER, null)),
                        Set.of("commentary"),
                        Map.ofEntries(on(withName(SUB_GER, "commentary")))
                ),
                Arguments.of(List.of(withName(SUB_GER, "commentary")),
                        Set.of("commentary"),
                        Map.ofEntries(on(withName(SUB_GER, "commentary")))
                ),
                Arguments.of(List.of(withName(AUDIO_GER_COMMENTARY, "commentary")),
                        Set.of("commentary"),
                        Map.ofEntries()
                ),
                Arguments.of(List.of(AUDIO_GER_COMMENTARY, withName(SUB_GER, "commentary")),
                        Set.of("commentary"),
                        Map.ofEntries(on(withName(SUB_GER, "commentary")))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("findCommentaryTracksAndApplyChanges")
    void findCommentaryTracksAndApplyChanges(List<TrackAttributes> tracks, Set<String> keywords, Map<TrackAttributes, Boolean> changes) {
        InputConfig.getInstance().setPreferredSubtitles(new HashSet<>());
        InputConfig.getInstance().setCommentaryKeywords(keywords);
        InputConfig.getInstance().setHearingImpaired(new HashSet<>());
        InputConfig.getInstance().setForcedKeywords(new HashSet<>());

        FileInfo fileInfo = new FileInfo(null);
        fileInfo.addTracks(tracks);
        AttributeProcessor.findCommentaryTracksAndApplyChanges(fileInfo);

        assertEquals(changes.size(), fileInfo.getChanges().getCommentaryTrack().size());
        changes.forEach((key, value) -> {
            assertTrue(fileInfo.getChanges().getCommentaryTrack().containsKey(key));
            assertEquals(value, fileInfo.getChanges().getCommentaryTrack().get(key));
        });
    }

    private static Stream<Arguments> findHearingImpairedTracksAndApplyChanges() {
        return Stream.of(
                Arguments.of(List.of(withName(SUB_GER, "SDH"), withName(SUB_GER, null)),
                        Set.of("SDH"),
                        Map.ofEntries(on(withName(SUB_GER, "SDH")))
                ),
                Arguments.of(List.of(withName(SUB_GER, "SDH")),
                        Set.of("SDH"),
                        Map.ofEntries(on(withName(SUB_GER, "SDH")))
                ),
                Arguments.of(List.of(withName(AUDIO_GER_COMMENTARY, "SDH")),
                        Set.of("SDH"),
                        Map.ofEntries()
                ),
                Arguments.of(List.of(AUDIO_GER_HEARING, withName(SUB_GER, "SDH")),
                        Set.of("SDH"),
                        Map.ofEntries(on(withName(SUB_GER, "SDH")))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("findCommentaryTracksAndApplyChanges")
    void findHearingImpairedTracksAndApplyChanges(List<TrackAttributes> tracks, Set<String> keywords, Map<TrackAttributes, Boolean> changes) {
        InputConfig.getInstance().setPreferredSubtitles(new HashSet<>());
        InputConfig.getInstance().setCommentaryKeywords(new HashSet<>());
        InputConfig.getInstance().setHearingImpaired(keywords);
        InputConfig.getInstance().setForcedKeywords(new HashSet<>());

        FileInfo fileInfo = new FileInfo(null);
        fileInfo.addTracks(tracks);
        AttributeProcessor.findHearingImpairedTracksAndApplyChanges(fileInfo);

        assertEquals(changes.size(), fileInfo.getChanges().getHearingImpairedTrack().size());
        changes.forEach((key, value) -> {
            assertTrue(fileInfo.getChanges().getHearingImpairedTrack().containsKey(key));
            assertEquals(value, fileInfo.getChanges().getHearingImpairedTrack().get(key));
        });
    }
}
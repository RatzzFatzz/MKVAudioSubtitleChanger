package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_DIR;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TrackAttributeUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SingleFileAttributeUpdaterTest {

    private static Stream<Arguments> process() {
        return Stream.of(
                Arguments.of(
                        arr(a("ger:OFF")), a("ger:OFF"),
                        List.of(AUDIO_GER, SUB_GER_FORCED),
                        Map.ofEntries(on(AUDIO_GER), on(SUB_GER_FORCED)),
                        Map.ofEntries(),
                        Map.ofEntries(),
                        Map.ofEntries()
                ),
                Arguments.of(
                        arr(a("ger:ger")), a("ger:ger"),
                        List.of(AUDIO_GER, SUB_GER, withName(AUDIO_GER, "SDH"), withName(AUDIO_GER, "commentary"), withName(SUB_GER, "Forced")),
                        Map.ofEntries(on(AUDIO_GER), on(SUB_GER)),
                        Map.ofEntries(on(withName(SUB_GER, "Forced"))),
                        Map.ofEntries(on(withName(AUDIO_GER, "commentary"))),
                        Map.ofEntries(on(withName(AUDIO_GER, "SDH")))
                ),
                Arguments.of(
                        arr(a("ger:OFF")), a("ger:OFF"),
                        List.of(AUDIO_GER, SUB_GER, withName(AUDIO_GER, "SDH"), withName(AUDIO_GER, "commentary"), withName(SUB_GER, "Forced")),
                        Map.ofEntries(on(AUDIO_GER), on(withName(SUB_GER, "Forced"))),
                        Map.ofEntries(on(withName(SUB_GER, "Forced"))),
                        Map.ofEntries(on(withName(AUDIO_GER, "commentary"))),
                        Map.ofEntries(on(withName(AUDIO_GER, "SDH")))
                ),
                Arguments.of(
                        arr(a("ger:eng")), null,
                        List.of(AUDIO_GER, SUB_GER, withName(AUDIO_GER, "SDH"), withName(AUDIO_GER, "commentary"), withName(SUB_GER, "Forced")),
                        Map.ofEntries(),
                        Map.ofEntries(on(withName(SUB_GER, "Forced"))),
                        Map.ofEntries(on(withName(AUDIO_GER, "commentary"))),
                        Map.ofEntries(on(withName(AUDIO_GER, "SDH")))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("process")
    void process(AttributeConfig[] attributeConfigs, AttributeConfig expectedMatch,
                 List<TrackAttributes> tracks,
                 Map<TrackAttributes, Boolean> defaultExp,
                 Map<TrackAttributes, Boolean> forcedExp,
                 Map<TrackAttributes, Boolean> commentaryExp,
                 Map<TrackAttributes, Boolean> hearingImpairedExp) {
        InputConfig config = new InputConfig();
        config.setThreads(1);
        config.setSafeMode(true);
        config.setAttributeConfig(attributeConfigs);
        FileInfo fileInfo = new FileInfo(new File(TEST_DIR));
        fileInfo.addTracks(tracks);
        FileProcessor fileProcessor = spy(FileProcessor.class);
        doReturn(fileInfo).when(fileProcessor).readAttributes(any());
        AttributeChangeProcessor attributeChangeProcessor = new AttributeChangeProcessor(new String[]{"pref"}, Set.of("forced"), Set.of("commentary"), Set.of("SDH"));
        SingleFileAttributeUpdater underTest = new SingleFileAttributeUpdater(config, fileProcessor, attributeChangeProcessor);

        underTest.process(fileInfo.getFile());

        assertEquals(expectedMatch, fileInfo.getMatchedConfig());
        assertEquals(fileInfo.getChanges().getDefaultTrack().size(), defaultExp.size());
        defaultExp.forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getDefaultTrack().get(key), "Default track flag"));

        assertEquals(fileInfo.getChanges().getForcedTrack().size(), forcedExp.size());
        forcedExp.forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getForcedTrack().get(key), "Forced track flag"));

        assertEquals(fileInfo.getChanges().getCommentaryTrack().size(), commentaryExp.size());
        commentaryExp.forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getCommentaryTrack().get(key), "Commentary track flag"));

        assertEquals(fileInfo.getChanges().getHearingImpairedTrack().size(), hearingImpairedExp.size());
        hearingImpairedExp.forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getHearingImpairedTrack().get(key), "Hearing Impaired track flag"));
    }
}
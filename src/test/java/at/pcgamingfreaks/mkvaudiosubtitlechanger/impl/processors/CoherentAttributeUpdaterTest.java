package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CommandRunner;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.LastExecutionHandler;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_DIR;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TrackAttributeUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoherentAttributeUpdaterTest {
    @Mock(lenient = true)
    FileProcessor fileProcessor;

    private static Stream<Arguments> findMatch() {
        return Stream.of(
                Arguments.of(AttributeConfig.of("ger", "ger"),
                        List.of(), false, 0),
                Arguments.of(AttributeConfig.of("ger", "ger"),
                        List.of(fileInfoMock("test.mkv", AUDIO_GER, SUB_GER)), true, 1),
                Arguments.of(AttributeConfig.of("ger", "ger"),
                        List.of(fileInfoMock("test.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test2.mkv", AUDIO_GER, SUB_GER)), true, 2),
                Arguments.of(AttributeConfig.of("ger", "ger"),
                        List.of(fileInfoMock("test.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test2.mkv", AUDIO_ENG, SUB_ENG)), false, 1),
                Arguments.of(AttributeConfig.of("ger", "ger"),
                        List.of(fileInfoMock("test.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test2.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test3.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test4.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test5.mkv", AUDIO_ENG, SUB_ENG)), false, 4),
                Arguments.of(AttributeConfig.of("ger", "ger"),
                        List.of(fileInfoMock("test.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test2.mkv", AUDIO_ENG, SUB_GER),
                                fileInfoMock("test3.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test4.mkv", AUDIO_GER, SUB_GER),
                                fileInfoMock("test5.mkv", AUDIO_GER, SUB_ENG)), false, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("findMatch")
    void findMatch(AttributeConfig attributeConfig, List<Pair<File, FileInfo>> fileInfoMock, boolean expectedMatch, int expectedMatchCount) throws InvocationTargetException, IllegalAccessException {
        CommandRunner commandRunner = new CommandRunner();
        new CommandLine(commandRunner).parseArgs("-a", "ger:ger", "/arst");
        InputConfig config = commandRunner.getConfig();
        AttributeChangeProcessor attributeChangeProcessor = new AttributeChangeProcessor(config.getPreferredSubtitles().toArray(new String[0]), config.getForcedKeywords(), config.getCommentaryKeywords(), config.getHearingImpaired());
        LastExecutionHandler lastExecutionHandler = new LastExecutionHandler("");
        CoherentAttributeUpdater updater = new CoherentAttributeUpdater(config, fileProcessor, attributeChangeProcessor, lastExecutionHandler);
        Set<FileInfo> matchedFiles = new HashSet<>(fileInfoMock.size() * 2);

        List<File> files = new ArrayList<>();
        for (Pair<File, FileInfo> pair : fileInfoMock) {
            when(fileProcessor.readAttributes(pair.getKey())).thenReturn(pair.getRight());
            files.add(pair.getKey());
        }

        Method underTest = Arrays.stream(updater.getClass().getDeclaredMethods()).filter(m -> "findMatch".equals(m.getName())).findFirst().get();
        underTest.setAccessible(true);
        AttributeConfig actualMatch = (AttributeConfig) underTest.invoke(updater, attributeConfig, matchedFiles, files);

        assertEquals(expectedMatch ? attributeConfig : null, actualMatch, "Matched AttributeConfig");
        assertEquals(expectedMatchCount, matchedFiles.size(), "Matched files count");
    }

    private static Pair<File, FileInfo> fileInfoMock(String path, TrackAttributes... tracks) {
        File file = new File(path);
        FileInfo fileInfo = new FileInfo(file);
        fileInfo.addTracks(List.of(tracks));
        return Pair.of(file, fileInfo);
    }

    private static Stream<Arguments> process() {
        return Stream.of(
                Arguments.of(
                        arr(a("ger:ger")), a("ger:ger"),
                        List.of(
                                List.of(AUDIO_GER, SUB_GER),
                                List.of(AUDIO_GER, SUB_GER)
                        ),
                        List.of(
                                Map.ofEntries(on(AUDIO_GER), on(SUB_GER)),
                                Map.ofEntries(on(AUDIO_GER), on(SUB_GER))
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        )
                ),
                Arguments.of(
                        arr(a("eng:eng"), a("ger:ger")), a("ger:ger"),
                        List.of(
                                List.of(SUB_ENG, AUDIO_GER, SUB_GER),
                                List.of(AUDIO_ENG, SUB_ENG, AUDIO_GER, SUB_GER)
                        ),
                        List.of(
                                Map.ofEntries(on(AUDIO_GER), on(SUB_GER)),
                                Map.ofEntries(on(AUDIO_GER), on(SUB_GER))
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        )
                ),
                Arguments.of(
                        arr(a("eng:eng"), a("ger:ger")), a("eng:eng"),
                        List.of(
                                List.of(AUDIO_ENG, withName(SUB_ENG, "SDH"), AUDIO_GER, SUB_GER),
                                List.of(AUDIO_ENG, SUB_ENG, AUDIO_GER, SUB_GER)
                        ),
                        List.of(
                                Map.ofEntries(on(AUDIO_ENG), on(withName(SUB_ENG, "SDH"))),
                                Map.ofEntries(on(AUDIO_ENG), on(SUB_ENG))
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        ),
                        List.of(
                                Map.ofEntries(),
                                Map.ofEntries()
                        ),
                        List.of(
                                Map.ofEntries(on(withName(SUB_ENG, "SDH"))),
                                Map.ofEntries()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("process")
    void process(AttributeConfig[] attributeConfigs, AttributeConfig expectedMatch,
                 List<List<TrackAttributes>> tracks,
                 List<Map<TrackAttributes, Boolean>> defaultExp,
                 List<Map<TrackAttributes, Boolean>> forcedExp,
                 List<Map<TrackAttributes, Boolean>> commentaryExp,
                 List<Map<TrackAttributes, Boolean>> hearingImpairedExp) {
        InputConfig config = new InputConfig();
        config.setThreads(1);
        config.setSafeMode(true);
        config.setAttributeConfig(attributeConfigs);
        FileProcessor fileProcessor = spy(FileProcessor.class);

        List<File> testMkvFiles = new ArrayList<>();
        List<FileInfo> testFileInfo = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            List<TrackAttributes> tracks1 = tracks.get(i);
            File file = new File(TEST_DIR + i);
            FileInfo fileInfo = new FileInfo(file);
            fileInfo.addTracks(tracks1);
            doReturn(fileInfo).when(fileProcessor).readAttributes(file);

            testMkvFiles.add(file);
            testFileInfo.add(fileInfo);
        }
        doReturn(testMkvFiles).when(fileProcessor).loadFiles(any());

        AttributeChangeProcessor attributeChangeProcessor = new AttributeChangeProcessor(new String[]{"pref"}, Set.of("forced"), Set.of("commentary"), Set.of("SDH"));
        LastExecutionHandler lastExecutionHandler = new LastExecutionHandler("");
        CoherentAttributeUpdater underTest = new CoherentAttributeUpdater(config, fileProcessor, attributeChangeProcessor, lastExecutionHandler);

        underTest.process(new File(""));

        for (int i = 0; i < testFileInfo.size(); i++) {
            FileInfo fileInfo = testFileInfo.get(i);
            assertEquals(expectedMatch, fileInfo.getMatchedConfig());
            assertEquals(fileInfo.getChanges().getDefaultTrack().size(), defaultExp.get(i).size());
            defaultExp.get(i).forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getDefaultTrack().get(key), "Default track flag"));

            assertEquals(fileInfo.getChanges().getForcedTrack().size(), forcedExp.get(i).size());
            forcedExp.get(i).forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getForcedTrack().get(key), "Forced track flag"));

            assertEquals(fileInfo.getChanges().getCommentaryTrack().size(), commentaryExp.get(i).size());
            commentaryExp.get(i).forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getCommentaryTrack().get(key), "Commentary track flag"));

            assertEquals(fileInfo.getChanges().getHearingImpairedTrack().size(), hearingImpairedExp.get(i).size());
            hearingImpairedExp.get(i).forEach((key, val) -> assertEquals(val, fileInfo.getChanges().getHearingImpairedTrack().get(key), "Hearing Impaired track flag"));
        }

    }
}
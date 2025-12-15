package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CommandRunner;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
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

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileInfoTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoherentAttributeUpdaterTest {
    @Mock(lenient = true)
    FileProcessor fileProcessor;

    @Test
    void process() {
    }

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
        new CommandLine(commandRunner).parseArgs("-l", "/arst", "-a", "ger:ger");
        InputConfig config = commandRunner.getConfig();
        CoherentAttributeUpdater updater = new CoherentAttributeUpdater(config, fileProcessor);
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
}
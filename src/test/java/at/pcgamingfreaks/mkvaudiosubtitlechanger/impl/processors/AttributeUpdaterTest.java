package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileInfoTestUtil.AUDIO_GER;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static org.junit.jupiter.api.Assertions.*;

class AttributeUpdaterTest {

    @BeforeEach
    void setup() {
        ResultStatistic.getInstance(true);
    }

    private static Stream<Arguments> checkStatusAndUpdate() {
        return Stream.of(
                Arguments.of(info(new AttributeConfig("ger", "ger"), AUDIO_GER), supplier(() -> ResultStatistic.getInstance().getChangePlanned())),
                Arguments.of(info(new AttributeConfig("ger", "ger"), null), supplier(() -> ResultStatistic.getInstance().getUnchanged())),
                Arguments.of(info(null, null), supplier(() -> ResultStatistic.getInstance().getUnchanged()))
        );
    }

    @ParameterizedTest
    @MethodSource("checkStatusAndUpdate")
    void checkStatusAndUpdate(FileInfo fileInfo, Supplier<Integer> getActual) {
        InputConfig config = new InputConfig();
        config.setThreads(1);
        config.setSafeMode(true);
        AttributeUpdater underTest = new AttributeUpdater(config, null, null) {
            @Override
            protected List<File> getFiles() {
                return List.of();
            }

            @Override
            protected void process(File file) {

            }
        };

        underTest.checkStatusAndUpdate(fileInfo);
        assertEquals(1, getActual.get());
    }

    private static Supplier<Integer> supplier(Supplier<Integer> supplier)  {
        return supplier;
    }

    private static FileInfo info(AttributeConfig config, TrackAttributes attr) {
        FileInfo fileInfo = new FileInfo(new File(TEST_FILE));
        fileInfo.setMatchedConfig(config);
        if(attr != null) fileInfo.getChanges().getDefaultTrack().put(attr, true);
        return fileInfo;
    }
}
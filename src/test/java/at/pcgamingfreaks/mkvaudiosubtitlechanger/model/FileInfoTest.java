package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileStatus.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileInfoTestUtil.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class FileInfoTest {

    private static Stream<Arguments> getStatus() {
        return Stream.of(
                Arguments.of(CHANGE_NECESSARY, info(new AttributeConfig("ger", "ger"), AUDIO_GER)),
                Arguments.of(ALREADY_SUITED, info(new AttributeConfig("ger", "ger"), null)),
                Arguments.of(NO_SUITABLE_CONFIG, info(null, null))
        );
    }

    @ParameterizedTest
    @MethodSource
    void getStatus(FileStatus expected, FileInfo underTest) {
        FileStatus actual = underTest.getStatus();
        assertEquals(expected, actual);
    }

    private static FileInfo info(AttributeConfig config, TrackAttributes attr) {
        FileInfo fileInfo = new FileInfo(null);
        fileInfo.setMatchedConfig(config);
        if(attr != null) fileInfo.getChanges().getDefaultTrack().put(attr, true);
        return fileInfo;
    }
}
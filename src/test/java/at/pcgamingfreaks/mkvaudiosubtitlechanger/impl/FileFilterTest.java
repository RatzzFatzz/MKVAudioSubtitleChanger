package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileFilterTest {
    @Mock(strictness = Mock.Strictness.LENIENT)
    File file;

    @Mock(strictness = Mock.Strictness.LENIENT)
    BasicFileAttributes attributes;

    @BeforeEach
    void beforeEach() {
        ResultStatistic.getInstance(true);
    }

    private static Stream<Arguments> accept() {
        return Stream.of(
                Arguments.of("~/video.mkv", Set.of(".mkv"), -1, ".*", true, 1, 0),
                Arguments.of("~/video.mp4", Set.of(".mkv"), -1, ".*", false, 0, 0),

                Arguments.of("~/video.mkv", Set.of(".mkv"), -1, "v.*", true,  1, 0),
                Arguments.of("~/video.mkv", Set.of(".mkv"), -1, "a.*", false, 1, 1),

                Arguments.of("~/video.mkv", Set.of(".mkv"), -1000, ".*", true, 1, 0),
                Arguments.of("~/video.mkv", Set.of(".mkv"), 1000, ".*", false, 1, 1)
        );
    }

    /**
     * @param filterDateOffset move filter data into the future or past by positive and negative values
     */
    @ParameterizedTest
    @MethodSource
    void accept(String path, Set<String> args, int filterDateOffset, String pattern, boolean expectedHit, int total, int excluded) {
        when(file.getAbsolutePath()).thenReturn(path);
        when(file.getName()).thenReturn(List.of(path.split("/")).get(1));
        when(file.toPath()).thenReturn(Path.of(TEST_FILE));

        long currentTime = System.currentTimeMillis();
        FileFilter fileFilter = new FileFilter(Set.of(), Pattern.compile(pattern), new Date(currentTime + filterDateOffset));

        try (MockedStatic<DateUtils> mockedFiles = Mockito.mockStatic(DateUtils.class)) {
            mockedFiles
                    .when(() -> DateUtils.convert(anyLong()))
                    .thenReturn(new Date(currentTime));

            assertEquals(expectedHit, fileFilter.accept(file, new HashSet<>(args)), "File is accepted");
            assertEquals(total, ResultStatistic.getInstance().getTotal(), "Total files");
            assertEquals(total, ResultStatistic.getInstance().total(), "Total files");
            assertEquals(excluded, ResultStatistic.getInstance().getExcluded(), "Excluded files");
        }
    }
}
package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.InputConfig;
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
import java.util.List;
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

    }

    private static Stream<Arguments> accept() {
        return Stream.of(
                Arguments.of("~/video.mkv", new String[]{".mkv"}, null, null, ".*", true),
                Arguments.of("~/video.mp4", new String[]{".mkv"}, null, null, ".*", false),

                Arguments.of("~/video.mkv", new String[]{".mkv"}, null, null, "v.*", true),
                Arguments.of("~/video.mkv", new String[]{".mkv"}, null, null, "a.*", false),

                Arguments.of("~/video.mkv", new String[]{".mkv"}, new Date(System.currentTimeMillis() - 1000), new Date(), ".*", false),
                Arguments.of("~/video.mkv", new String[]{".mkv"}, new Date(), new Date(System.currentTimeMillis() - 1000), ".*", true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void accept(String path, String[] args, Date fileCreationDate, Date filterDate, String pattern, boolean expected) {
        when(file.getAbsolutePath()).thenReturn(path);
        when(file.getName()).thenReturn(List.of(path.split("/")).get(1));
        when(file.toPath()).thenReturn(Path.of(TEST_FILE));

        InputConfig.getInstance(true).setIncludePattern(Pattern.compile(pattern));
        if (filterDate != null) InputConfig.getInstance().setFilterDate(filterDate);

        try (MockedStatic<DateUtils> mockedFiles = Mockito.mockStatic(DateUtils.class)) {
            mockedFiles
                    .when(() -> DateUtils.convert(anyLong()))
                    .thenReturn(fileCreationDate);

            assertEquals(expected, FileFilter.accept(file, args));
        }
    }
}
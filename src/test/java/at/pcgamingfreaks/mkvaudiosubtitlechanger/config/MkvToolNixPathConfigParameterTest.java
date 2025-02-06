package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static org.junit.jupiter.api.Assertions.*;

class MkvToolNixPathConfigParameterTest {

    private static final String TEST_INVALID_DIR = "src/test/resources/test-dir";
    private static final String TEST_MKVTOOLNIX_DIR = "src/test/resources/mkvtoolnix";
    private static final String TEST_MKVTOOLNIX_EXE_DIR = "src/test/resources/mkvtoolnix_exe";

    private static Stream<Arguments> provideTestCases() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return Stream.of(
                    Arguments.of(args("-m", TEST_MKVTOOLNIX_EXE_DIR), TEST_MKVTOOLNIX_EXE_DIR, (Function<Config, File>) Config::getMkvToolNix),
                    Arguments.of(args("--mkvtoolnix", TEST_MKVTOOLNIX_EXE_DIR), TEST_MKVTOOLNIX_EXE_DIR, (Function<Config, File>) Config::getMkvToolNix)
            );
        }

        return Stream.of(
                Arguments.of(args("-m", TEST_MKVTOOLNIX_DIR), TEST_MKVTOOLNIX_DIR, (Function<Config, File>) Config::getMkvToolNix),
                Arguments.of(args("--mkvtoolnix", TEST_MKVTOOLNIX_DIR), TEST_MKVTOOLNIX_DIR, (Function<Config, File>) Config::getMkvToolNix)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, String expected, Function<Config, File> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(Path.of(expected).toFile().getAbsolutePath(), fieldUnderTest.apply(sut.getConfig()).getAbsolutePath());
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-m", TEST_INVALID_DIR)));
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-m")));
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("")));
    }
}
package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.fields;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.InputConfig;
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

    private static final String TEST_MKVTOOLNIX_DIR = SystemUtils.IS_OS_WINDOWS ? "src/test/resources/mkvtoolnix_exe" : "src/test/resources/mkvtoolnix";

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-m", TEST_MKVTOOLNIX_DIR), TEST_MKVTOOLNIX_DIR, (Function<InputConfig, File>) InputConfig::getMkvToolNix),
                Arguments.of(args("--mkvtoolnix", TEST_MKVTOOLNIX_DIR), TEST_MKVTOOLNIX_DIR, (Function<InputConfig, File>) InputConfig::getMkvToolNix)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, String expected, Function<InputConfig, File> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(Path.of(expected).toFile().getAbsolutePath(), fieldUnderTest.apply(sut.getConfig()).getAbsolutePath());
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-m")));
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("")));
    }
}
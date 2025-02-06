package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_DIR;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathConfigParameterTest {

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-l", TEST_DIR), Path.of(TEST_DIR).toFile(), true, (Function<Config, File>) Config::getLibraryPath),
                Arguments.of(args("-l", TEST_FILE), Path.of(TEST_FILE).toFile(), true, (Function<Config, File>) Config::getLibraryPath)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, File expected, boolean exists, Function<Config, File> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(expected.getAbsolutePath(), fieldUnderTest.apply(sut.getConfig()).getAbsolutePath());
        assertEquals(exists, fieldUnderTest.apply(sut.getConfig()).exists());
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-l", "arst")));
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-l")));
        assertThrows(CommandLine.UnmatchedArgumentException.class, () -> CommandLine.populateCommand(sut, args("")));
    }

    private static String[] args(String... args) {
        String[] staticArray = new String[]{"-a", "ger:ger"};
        String[] result = new String[staticArray.length + args.length];
        System.arraycopy(staticArray, 0, result, 0, staticArray.length);
        System.arraycopy(args, 0, result, staticArray.length, args.length);
        return result;
    }
}
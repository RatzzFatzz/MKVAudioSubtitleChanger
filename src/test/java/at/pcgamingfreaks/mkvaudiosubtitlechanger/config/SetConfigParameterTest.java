package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetConfigParameterTest {

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-ck", "test"), 1, (Function<Config, Set<String>>) Config::getCommentaryKeywords),
                Arguments.of(args("-ck", "test", "test1", "test2", "test3", "test4"), 5, (Function<Config, Set<String>>) Config::getCommentaryKeywords),
                Arguments.of(args(), 2, (Function<Config, Set<String>>) Config::getCommentaryKeywords),
                Arguments.of(args("-fk", "test"), 1, (Function<Config, Set<String>>) Config::getForcedKeywords),
                Arguments.of(args("-fk", "test", "test1", "test2", "test3", "test4"), 5, (Function<Config, Set<String>>) Config::getForcedKeywords),
                Arguments.of(args(), 3, (Function<Config, Set<String>>) Config::getForcedKeywords),
                Arguments.of(args("-ps", "test"), 1, (Function<Config, Set<String>>) Config::getPreferredSubtitles),
                Arguments.of(args("-ps", "test", "test1", "test2", "test3", "test4"), 5, (Function<Config, Set<String>>) Config::getPreferredSubtitles),
                Arguments.of(args(), 1, (Function<Config, Set<String>>) Config::getPreferredSubtitles)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, int expectedSize, Function<Config, Set<String>> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(expectedSize, fieldUnderTest.apply(sut.getConfig()).size());
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-ck")));
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-fk")));
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-e")));
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-ps")));
    }
}
package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatternConfigParameterTest {

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-i", "[abd]?.*"), Pattern.compile("[abd]?.*"), (Function<Config, Pattern>) Config::getIncludePattern),
                Arguments.of(args("-i", ".*"), Pattern.compile(".*"), (Function<Config, Pattern>) Config::getIncludePattern),
                Arguments.of(args(), Pattern.compile(".*"), (Function<Config, Pattern>) Config::getIncludePattern)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, Pattern expected, Function<Config, Pattern> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(expected.pattern(), fieldUnderTest.apply(sut.getConfig()).pattern());
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-i")));
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-i", "[")));
    }
}
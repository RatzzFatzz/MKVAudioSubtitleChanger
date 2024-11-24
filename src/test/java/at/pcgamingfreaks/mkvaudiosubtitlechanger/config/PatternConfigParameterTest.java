package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.INCLUDE_PATTERN;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.*;

class PatternConfigParameterTest {
    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(INCLUDE_PATTERN, INCLUDE_PATTERN.abrv(), INCLUDE_PATTERN.args()));
    }

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
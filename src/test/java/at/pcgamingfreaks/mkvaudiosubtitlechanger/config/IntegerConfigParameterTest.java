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
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.THREADS;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.*;

class IntegerConfigParameterTest {

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args(), 2, (Function<Config, Integer>) Config::getThreads),
                Arguments.of(args("-t", "5"), 5, (Function<Config, Integer>) Config::getThreads),
                Arguments.of(args("--threads", "5"), 5, (Function<Config, Integer>) Config::getThreads)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, int expected, Function<Config, Integer> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(expected, fieldUnderTest.apply(sut.getConfig()));
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("-t")));
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, args("--threads")));
        assertThrows(CommandLine.ParameterException.class, () -> Main.main(args("--threads", "0")));
    }
}
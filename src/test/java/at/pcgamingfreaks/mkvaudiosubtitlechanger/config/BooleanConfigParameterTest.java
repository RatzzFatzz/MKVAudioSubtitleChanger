package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.function.Function;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class BooleanConfigParameterTest {
    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(SAFE_MODE, SAFE_MODE.abrv(), SAFE_MODE.args()));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-s"), true, (Function<Config, Boolean>) Config::isSafeMode),
                Arguments.of(args("--safemode"), true, (Function<Config, Boolean>) Config::isSafeMode),
                Arguments.of(args(), false, (Function<Config, Boolean>) Config::isSafeMode),
                Arguments.of(args("-cf"), true, (Function<Config, Boolean>) Config::isForceCoherent),
                Arguments.of(args("--force-coherent"), true, (Function<Config, Boolean>) Config::isForceCoherent),
                Arguments.of(args(), false, (Function<Config, Boolean>) Config::isForceCoherent),
                Arguments.of(args("-n"), true, (Function<Config, Boolean>) Config::isOnlyNewFiles),
                Arguments.of(args(), false, (Function<Config, Boolean>) Config::isOnlyNewFiles)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, boolean expected, Function<Config, Boolean> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(expected, fieldUnderTest.apply(sut.getConfig()));
    }
}
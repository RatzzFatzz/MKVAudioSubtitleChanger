package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.function.Function;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BooleanConfigParameterTest {

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-s"), true, (Function<InputConfig, Boolean>) InputConfig::isSafeMode),
                Arguments.of(args("--safemode"), true, (Function<InputConfig, Boolean>) InputConfig::isSafeMode),
                Arguments.of(args(), false, (Function<InputConfig, Boolean>) InputConfig::isSafeMode),
                Arguments.of(args("-cf"), true, (Function<InputConfig, Boolean>) InputConfig::isForceCoherent),
                Arguments.of(args("--force-coherent"), true, (Function<InputConfig, Boolean>) InputConfig::isForceCoherent),
                Arguments.of(args(), false, (Function<InputConfig, Boolean>) InputConfig::isForceCoherent),
                Arguments.of(args("-n"), true, (Function<InputConfig, Boolean>) InputConfig::isOnlyNewFiles),
                Arguments.of(args(), false, (Function<InputConfig, Boolean>) InputConfig::isOnlyNewFiles)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, boolean expected, Function<InputConfig, Boolean> fieldUnderTest) {
        Main sut = new Main();
        CommandLine.populateCommand(sut, cmdArgs);
        assertEquals(expected, fieldUnderTest.apply(sut.getConfig()));
    }
}
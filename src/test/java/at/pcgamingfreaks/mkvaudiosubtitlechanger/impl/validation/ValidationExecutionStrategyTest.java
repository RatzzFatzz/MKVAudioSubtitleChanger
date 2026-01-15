package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CommandRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class ValidationExecutionStrategyTest {

    @Test
    void validate() {
        CommandRunner underTest = new CommandRunner();
        new CommandLine(underTest)
                .setExecutionStrategy(new ValidationExecutionStrategy())
                .parseArgs("-a", "ger:ger", "-m", TEST_MKVTOOLNIX_DIR, TEST_FILE);

        assertEquals(TEST_FILE, underTest.getConfig().getLibraryPath()[0].getPath().replace("\\", "/"));
        assertEquals(TEST_MKVTOOLNIX_DIR, underTest.getConfig().getMkvToolNix().getPath().replace("\\", "/"));
    }

    private static Stream<Arguments> validateFailure() {
        return Stream.of(
                Arguments.of(new String[]{"-a", "jpn:ger"}, "Error: Missing required argument(s): <libraryPath>"),
                Arguments.of(new String[]{"/arstarstarst"}, "libraryPath does not exist"),
                Arguments.of(new String[]{TEST_DIR, "/arstarstarst"}, "libraryPath does not exist"),
                Arguments.of(new String[]{"/arstarstarst", "-a",}, "Missing required parameter for option '--attribute-config' at index 0 (<attributeConfig>)"),
                Arguments.of(new String[]{"/arstarstarst", "-a", "jpn:ger"}, "libraryPath does not exist"),
                Arguments.of(new String[]{"/arstarstarst", "-m"}, "Missing required parameter for option '--mkvtoolnix' (<mkvToolNix>)"),
                Arguments.of(new String[]{"./", "-m", TEST_INVALID_DIR}, "mkvToolNix does not exist"),
                Arguments.of(new String[]{"./", "-t"}, "Missing required parameter for option '--threads' (<threads>)"),
                Arguments.of(new String[]{"./", "-t", "0"}, "threads must be greater than or equal to 1"),
                Arguments.of(new String[]{"./", "-t", "-1"}, "threads must be greater than or equal to 1"),
                Arguments.of(new String[]{"./", "-c", "-1"}, "coherent must be greater than or equal to 0")
        );
    }

    @ParameterizedTest
    @MethodSource("validateFailure")
    void validateFailure(String[] args, String expectedMessage) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        new CommandLine(CommandRunner.class)
                .setExecutionStrategy(new ValidationExecutionStrategy())
                .setErr(printWriter)
                .execute(args);

        printWriter.flush();
        assertEquals(expectedMessage, writer.toString().split("[\r\n]")[0]);
    }
}
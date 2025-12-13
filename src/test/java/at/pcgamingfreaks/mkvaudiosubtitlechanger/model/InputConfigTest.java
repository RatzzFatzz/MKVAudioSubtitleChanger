package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation.ValidationExecutionStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static org.junit.jupiter.api.Assertions.*;

class InputConfigTest {
    private static final String TEST_INVALID_DIR = "src/test/resources/test-dir";

    @Test
    void initConfig() {
        String[] sut = new String[]{"-a", "ger:ger", "eng:eng", "-l", TEST_FILE,
                "-s", "-cf", "-n",
                "-c", "2",
                "-t", "4",
                "-i", ".*[abc].*",
                "--forced-keywords", "testForced",
                "--commentary-keywords", "testCommentary",
                "--preferred-subtitles", "testPreferred"
        };
        InputConfig config = CommandLine.populateCommand(new InputConfig(), sut);

        assertTrue(config.getLibraryPath().exists());
        assertArrayEquals(new AttributeConfig[]{new AttributeConfig("ger", "ger"), new AttributeConfig("eng", "eng")},
                config.getAttributeConfig());

        assertTrue(config.isSafeMode());
        assertTrue(config.isForceCoherent());
        assertTrue(config.isOnlyNewFiles());
        assertNull(config.getFilterDate());

        assertEquals(2, config.getCoherent());
        assertEquals(4, config.getThreads());
        assertEquals(".*[abc].*", config.getIncludePattern().pattern());
        assertTrue(config.getForcedKeywords().contains("testForced"));
        assertTrue(config.getCommentaryKeywords().contains("testCommentary"));
        assertTrue(config.getPreferredSubtitles().contains("testPreferred"));

        assertNull(config.getConfigPath());
    }


    private static Stream<Arguments> jakartaValidationData() {
        return Stream.of(
            Arguments.of(new String[]{"-l", "/arstarstarst", "-a", "jpn:ger"}, "libraryPath does not exist"),
            Arguments.of(args("-m", TEST_INVALID_DIR), "mkvToolNix does not exist"),
            Arguments.of(args("-t", "0"), "threads must be greater than or equal to 1"),
            Arguments.of(args("-t", "-1"), "threads must be greater than or equal to 1"),
            Arguments.of(args("-c", "-1"), "coherent must be greater than or equal to 0")
        );
    }

    @ParameterizedTest
    @MethodSource("jakartaValidationData")
    void testJakartaValidation(String[] args, String expectedMessage) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        new CommandLine(Main.class)
                .setExecutionStrategy(new ValidationExecutionStrategy())
                .setErr(printWriter)
                .execute(args);

        printWriter.flush();
        assertTrue(writer.toString().contains(expectedMessage));
    }
}
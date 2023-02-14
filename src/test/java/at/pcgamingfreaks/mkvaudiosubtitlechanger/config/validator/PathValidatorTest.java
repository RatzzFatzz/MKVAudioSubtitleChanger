package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.INVALID;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.LIBRARY;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.*;

class PathValidatorTest {
    private static final String TEST_DIR = "src/test/resources/test-dir";
    private static final String TEST_FILE = "src/test/resources/test-dir/test-file.mkv";

    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(LIBRARY, LIBRARY.abrv(), true));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                argumentsOf(LIBRARY, false, null, "library: " + TEST_DIR, new String[]{}, VALID),
                argumentsOf(LIBRARY, true, null, "", new String[]{"-l", TEST_FILE}, VALID),

                argumentsOf(LIBRARY, false, TEST_DIR, "", new String[]{}, DEFAULT),
                argumentsOf(LIBRARY, true, TEST_FILE, "", new String[]{}, DEFAULT),
                argumentsOf(LIBRARY, true, null, "", new String[]{}, MISSING),
                argumentsOf(LIBRARY, false, null, "", new String[]{}, NOT_PRESENT),

                argumentsOf(LIBRARY, true, null, "", new String[]{"-l", TEST_DIR + "/invalid"}, INVALID),
                argumentsOf(LIBRARY, false, null, "library: " + TEST_DIR + "/invalid", new String[]{}, INVALID),
                argumentsOf(LIBRARY, true, TEST_DIR, "", new String[]{"-l", TEST_DIR + "/invalid"}, INVALID)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, File defaultValue, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult) throws ParseException, YamlInvalidContentException {
        PathValidator underTest = new PathValidator(property, required, defaultValue);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
    }
}
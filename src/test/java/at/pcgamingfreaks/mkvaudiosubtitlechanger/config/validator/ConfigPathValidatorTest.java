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

import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.INVALID;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.CONFIG_PATH;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static org.junit.jupiter.api.Assertions.*;

class ConfigPathValidatorTest {
    private static final String TEST_FILE = "src/test/resources/test-dir/test-file.mkv";
    private static final String TEST_CONFIG = "src/test/resources/test-dir/test-config.yaml";

    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(CONFIG_PATH, CONFIG_PATH.abrv(), CONFIG_PATH.args()));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(CONFIG_PATH, true, "", new String[]{"-p", TEST_CONFIG}, VALID),
                Arguments.of(CONFIG_PATH, true, "config-path: " + TEST_CONFIG, new String[]{}, MISSING),
                Arguments.of(CONFIG_PATH, false, "config-path: " + TEST_CONFIG, new String[]{}, NOT_PRESENT),
                Arguments.of(CONFIG_PATH, true, "", new String[]{"-p", TEST_FILE}, INVALID)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult) throws ParseException, YamlInvalidContentException {
        ConfigPathValidator underTest = new ConfigPathValidator(property, required);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
    }
}
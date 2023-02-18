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

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.INVALID;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.INCLUDE_PATTERN;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.*;

class PatternValidatorTest {
    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(INCLUDE_PATTERN, INCLUDE_PATTERN.abrv(), true));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                argumentsOf(INCLUDE_PATTERN, false, null, "include-pattern: \"[abd]?.*\"", new String[]{}, VALID),
                argumentsOf(INCLUDE_PATTERN, true, null, "", new String[]{"-i", "[abd]?.*"}, VALID),

                argumentsOf(INCLUDE_PATTERN, false, Pattern.compile(".*"), "", new String[]{}, DEFAULT),
                argumentsOf(INCLUDE_PATTERN, true, Pattern.compile(".*"), "", new String[]{}, DEFAULT),

                argumentsOf(INCLUDE_PATTERN, true, null, "", new String[]{}, MISSING),
                argumentsOf(INCLUDE_PATTERN, false, null, "", new String[]{}, NOT_PRESENT),

                argumentsOf(INCLUDE_PATTERN, true, null, "", new String[]{"-i", "?."}, INVALID),
                argumentsOf(INCLUDE_PATTERN, false, null, "include-pattern: \"[arst*\"", new String[]{}, INVALID),
                argumentsOf(INCLUDE_PATTERN, true, Pattern.compile(".?"), "", new String[]{"-i", "?."}, INVALID)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, Pattern defaultValue, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult) throws ParseException, YamlInvalidContentException {
        PatternValidator underTest = new PatternValidator(property, required, defaultValue);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
    }
}
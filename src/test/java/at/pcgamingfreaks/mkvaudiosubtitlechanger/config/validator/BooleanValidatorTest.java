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
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.yamlList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BooleanValidatorTest {
    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(SAFE_MODE, SAFE_MODE.abrv(), false));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(SAFE_MODE, false, "", new String[]{"-safe-mode"}, VALID),
                Arguments.of(SAFE_MODE, true, "", new String[]{"-safe-mode"}, VALID),
                Arguments.of(SAFE_MODE, false, "", new String[]{""}, NOT_PRESENT),
                Arguments.of(SAFE_MODE, true, "", new String[]{""}, MISSING),
                Arguments.of(SAFE_MODE, false, yamlList(ARGUMENTS, SAFE_MODE), new String[]{""}, VALID),
                Arguments.of(SAFE_MODE, true, yamlList(ARGUMENTS, SAFE_MODE), new String[]{""}, VALID),
                Arguments.of(SAFE_MODE, false, yamlList(ARGUMENTS, WINDOWS), new String[]{""}, NOT_PRESENT),
                Arguments.of(SAFE_MODE, true, yamlList(ARGUMENTS, WINDOWS), new String[]{""}, MISSING)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult) throws ParseException, YamlInvalidContentException {
        BooleanValidator underTest = new BooleanValidator(property, required);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
    }
}
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
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.THREADS;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadValidatorTest {

    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(THREADS, "t", THREADS.args()));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                argumentsOf(THREADS, false, null, "", new String[]{"-t", "10"}, VALID),
                argumentsOf(THREADS, true, null, "", new String[]{"-t", "10"}, VALID),
                argumentsOf(THREADS, false, null, "threads: 10", new String[]{}, VALID),
                argumentsOf(THREADS, true, null, "threads: 10", new String[]{}, VALID),
                argumentsOf(THREADS, false, 2, "", new String[]{}, DEFAULT),
                argumentsOf(THREADS, true, null, "", new String[]{}, MISSING),
                argumentsOf(THREADS, false, null, "", new String[]{}, NOT_PRESENT),
                argumentsOf(THREADS, true, null, "", new String[]{"-t", "-1"}, INVALID),
                argumentsOf(THREADS, true, null, "threads: 0", new String[]{}, INVALID),
                argumentsOf(THREADS, true, 2, "", new String[]{"-t", "0"}, INVALID)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, Integer defaultValue, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult) throws ParseException, YamlInvalidContentException {
        ThreadValidator underTest = new ThreadValidator(property, required, defaultValue);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
    }
}
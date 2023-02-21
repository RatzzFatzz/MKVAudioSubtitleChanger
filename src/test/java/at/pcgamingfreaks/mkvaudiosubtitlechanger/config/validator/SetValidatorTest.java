package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.COMMENTARY_KEYWORDS;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.argumentsOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SetValidatorTest {

    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(COMMENTARY_KEYWORDS, COMMENTARY_KEYWORDS.abrv(), COMMENTARY_KEYWORDS.args()));
    }

    @BeforeEach
    void beforeEach() {
        Config.getInstance(true);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                argumentsOf(COMMENTARY_KEYWORDS, true, true, "", new String[]{"-ck", "test"}, VALID, 3),
                argumentsOf(COMMENTARY_KEYWORDS, true, false, COMMENTARY_KEYWORDS.prop() + ": [test]", new String[]{}, VALID, 1),
                argumentsOf(COMMENTARY_KEYWORDS, true, false, COMMENTARY_KEYWORDS.prop() + ":\n - test\n - test2", new String[]{}, VALID, 2),
                argumentsOf(COMMENTARY_KEYWORDS, false, true, COMMENTARY_KEYWORDS.prop() + ": [test]", new String[]{}, VALID, 3),
                argumentsOf(COMMENTARY_KEYWORDS, false, false, "", new String[]{"-ck", "test"}, VALID, 1),
                argumentsOf(COMMENTARY_KEYWORDS, true, true, COMMENTARY_KEYWORDS.prop() + ": [commentary]", new String[]{}, VALID, 2),

                argumentsOf(COMMENTARY_KEYWORDS, true, true, "", new String[]{}, MISSING, 2),
                argumentsOf(COMMENTARY_KEYWORDS, false, true, "", new String[]{}, NOT_PRESENT, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, boolean append, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult, int expectedSize) throws ParseException, YamlInvalidContentException {
        SetValidator underTest = new SetValidator(property, required, append);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
        assertEquals(expectedSize, Config.getInstance().getCommentaryKeywords().size());
    }
}
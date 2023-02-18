package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static org.junit.jupiter.api.Assertions.*;

class AttributeConfigValidatorTest {
    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(ATTRIBUTE_CONFIG, ATTRIBUTE_CONFIG.abrv(), true));
    }

    @BeforeEach
    void beforeEach() {
        Config.getInstance(true);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(attrConfYaml("jpn", "ger"), new String[]{}, VALID, attrConf("jpn", "ger")),
                Arguments.of(attrConfYaml("jpn", "ger", "jpn", "eng"), new String[]{}, VALID, attrConf("jpn", "ger", "jpn", "eng")),
                Arguments.of(attrConfYaml("jpn", "ger", "jpn", "OFF"), new String[]{}, VALID, attrConf("jpn", "ger", "jpn", "OFF")),
                Arguments.of(attrConfYaml("jpn", "invalid"), new String[]{}, INVALID, null),
                Arguments.of("", new String[]{}, MISSING, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String yamlArgs, String[] cmdArgs, ValidationResult expectedResult, List<AttributeConfig> expectedConfig)
            throws ParseException, YamlInvalidContentException {
        AttributeConfigValidator underTest = new AttributeConfigValidator();

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
        assertIterableEquals(expectedConfig, Config.getInstance().getAttributeConfig());
    }

    private static String attrConfYaml(String... languages) {
        StringBuilder yaml = new StringBuilder("attribute-config: ");
        int counter = 0;
        for (int i = 0; i < languages.length; i += 2) {
            counter++;
            yaml.append(String.format("\n %s:\n  audio: %s\n  subtitle: %s", counter, languages[0], languages[1]));
        }
        return yaml.toString();
    }

    private static List<AttributeConfig> attrConf(String... languages) {
        List<AttributeConfig> conf = new ArrayList<>();
        for (int i = 0; i < languages.length; i += 2) {
            conf.add(new AttributeConfig(languages[0], languages[1]));
        }
        return conf;
    }
}
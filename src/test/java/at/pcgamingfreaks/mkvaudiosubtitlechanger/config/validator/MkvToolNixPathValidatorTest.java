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
import java.nio.file.Path;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult.MISSING;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static org.junit.jupiter.api.Assertions.*;

class MkvToolNixPathValidatorTest {
    private static final String TEST_INVALID_DIR = "src/test/resources/test-dir";
    private static final String TEST_MKVTOOLNIX_DIR = "src/test/resources/mkvtoolnix";
    private static final String TEST_MKVTOOLNIX_EXE_DIR = "src/test/resources/mkvtoolnix_exe";

    private static CommandLineParser parser;
    private static Options options;

    @BeforeAll
    static void before() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption(optionOf(MKV_TOOL_NIX, MKV_TOOL_NIX.abrv(), MKV_TOOL_NIX.args()));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(MKV_TOOL_NIX, false, null, "", new String[]{"-m", TEST_MKVTOOLNIX_DIR}, VALID),
                Arguments.of(MKV_TOOL_NIX, true, null, "", new String[]{"-m", TEST_MKVTOOLNIX_EXE_DIR}, VALID),
                Arguments.of(MKV_TOOL_NIX, false, null, "mkvtoolnix: " + TEST_MKVTOOLNIX_EXE_DIR, new String[]{}, VALID),
                Arguments.of(MKV_TOOL_NIX, true, null, "mkvtoolnix: " + TEST_MKVTOOLNIX_DIR, new String[]{}, VALID),
                Arguments.of(MKV_TOOL_NIX, false, Path.of(TEST_MKVTOOLNIX_EXE_DIR).toFile(), "", new String[]{}, DEFAULT),
                Arguments.of(MKV_TOOL_NIX, false, null, "", new String[]{}, NOT_PRESENT),
                Arguments.of(MKV_TOOL_NIX, true, null, "", new String[]{}, MISSING),
                Arguments.of(MKV_TOOL_NIX, true, null, "", new String[]{"-m", TEST_INVALID_DIR}, INVALID)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(ConfigProperty property, boolean required, File defaultValue, String yamlArgs, String[] cmdArgs,
                  ValidationResult expectedResult) throws ParseException, YamlInvalidContentException {
        MkvToolNixPathValidator underTest = new MkvToolNixPathValidator(property, required, defaultValue);

        ValidationResult result = underTest.validate(new YAML(yamlArgs), parser.parse(options, cmdArgs));

        assertEquals(expectedResult, result);
    }

}
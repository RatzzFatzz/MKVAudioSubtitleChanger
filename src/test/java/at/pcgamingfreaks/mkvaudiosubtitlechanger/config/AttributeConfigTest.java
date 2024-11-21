package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.*;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static org.junit.jupiter.api.Assertions.*;

class AttributeConfigTest {

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(args("-a", "jpn:ger"), attrConf("jpn", "ger")),
                Arguments.of(args("-a", "jpn:ger", "jpn:eng"), attrConf("jpn", "ger", "jpn", "eng")),
                Arguments.of(args("-a", "jpn:ger", "jpn:OFF"), attrConf("jpn", "ger", "jpn", "OFF"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void validate(String[] cmdArgs, List<AttributeConfig> expectedConfig) {
        Main underTest = new Main();
        CommandLine.populateCommand(underTest, cmdArgs);
        assertIterableEquals(expectedConfig, underTest.getConfig().getAttributeConfig());
    }

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, new String[]{"-l", "/"}));
        assertThrows(CommandLine.MissingParameterException.class, () -> CommandLine.populateCommand(sut, new String[]{"-l", "/", "-a"}));
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, new String[]{"-l", "/", "-a", "ger:"}));
        assertThrows(CommandLine.ParameterException.class,
                () -> CommandLine.populateCommand(sut, new String[]{"-l", "/", "-a", "ger:qwf"})); // Invalid language code
    }

    private static String[] args(String... args) {
        String[] staticArray = new String[]{"-l", "/"};
        String[] result = new String[staticArray.length + args.length];
        System.arraycopy(staticArray, 0, result, 0, staticArray.length);
        System.arraycopy(args, 0, result, staticArray.length, args.length);
        return result;
    }

    private static List<AttributeConfig> attrConf(String... languages) {
        List<AttributeConfig> conf = new ArrayList<>();
        for (int i = 0; i < languages.length; i += 2) {
            conf.add(new AttributeConfig(languages[i], languages[i+1]));
        }
        return conf;
    }
}
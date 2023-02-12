package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class TestUtil {
    public static String yamlList(ConfigProperty main, ConfigProperty... child) {
        return main.prop() + ":\n" + Arrays.stream(child)
                .map(ConfigProperty::prop)
                .collect(joining("\n", "  - ", ""));
    }

    public static <T> Arguments argumentsOf(ConfigProperty property, boolean required, T defaultValue, String yaml, String[] cmd,
                                           ValidationResult result) {
        return Arguments.of(property, required, defaultValue, yaml, cmd, result);
    }

    public static Arguments argumentsOf(ConfigProperty property, boolean required, boolean append, String yaml, String[] cmd,
                                            ValidationResult result, int expectedSize) {
        return Arguments.of(property, required, append, yaml, cmd, result, expectedSize);
    }

}

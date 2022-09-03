package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class TestUtil {
    public static String yamlList(ConfigProperty main, ConfigProperty... child) {
        return main.prop() + ":\n" + Arrays.stream(child)
                .map(ConfigProperty::prop)
                .collect(joining("\n", "  - ", ""));
    }

}

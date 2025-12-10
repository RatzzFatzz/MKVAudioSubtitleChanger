package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.converter;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.converter.AttributeConfigConverter;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AttributeConfigConverterTest {

    private static Stream<Arguments> validData() {
        return Stream.of(
                Arguments.of("jpn:ger", new AttributeConfig("jpn", "ger")),
                Arguments.of("eng:eng", new AttributeConfig("eng", "eng")),
                Arguments.of("OFF:OFF", new AttributeConfig("OFF", "OFF"))
        );
    }

    @ParameterizedTest
    @MethodSource("validData")
    void convert(String input, AttributeConfig expected) {
        AttributeConfigConverter underTest = new AttributeConfigConverter();
        AttributeConfig actual = underTest.convert(input);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> invalidData() {
        return Stream.of(
                Arguments.of("ars:eng"),
                Arguments.of("ars:OFF"),
                Arguments.of("OFF:ars"),
                Arguments.of("ars:ars"),
                Arguments.of("arss:ars"),
                Arguments.of("ars:arsr")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidData")
    void convertInvalid(String input) {
        AttributeConfigConverter underTest = new AttributeConfigConverter();
        assertThrows(CommandLine.TypeConversionException.class, () -> underTest.convert(input));
    }
}


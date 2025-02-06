package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import picocli.CommandLine;

import java.util.regex.Pattern;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.LanguageValidatorUtil.isAudioLanguageValid;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.LanguageValidatorUtil.isLanguageValid;

public class AttributeConfigConverter implements CommandLine.ITypeConverter<AttributeConfig> {
    private static final String SEPARATOR = ":";
    private static final Pattern PATTERN = Pattern.compile("^.{3}:.{3}$");

    /**
     * Converts the input string into an AttributeConfig object.
     *
     * @param s The input string containing audio and subtitle language configuration in format "audioLang:subtitleLang"
     * @return An AttributeConfig object representing the parsed configuration
     * @throws CommandLine.TypeConversionException if the input string is invalid or contains invalid language codes
     */
    @Override
    public AttributeConfig convert(String s) throws Exception {
        validateInput(s);

        String[] split = s.split(SEPARATOR);
        AttributeConfig attr = new AttributeConfig(split[0], split[1]);

        validateResult(attr);

        return attr;
    }

    /**
     * Validates that the input string matches the expected pattern.
     *
     * @param s String to validate
     * @throws CommandLine.TypeConversionException if the value doesn't match the expected pattern
     */
    private static void validateInput(String s) {
        if (!PATTERN.matcher(s).matches()) {
            throw new CommandLine.TypeConversionException("Invalid Attribute config: " + s);
        }
    }

    /**
     * Validates that both language codes in the AttributeConfig object are valid.
     *
     * @param attr AttributeConfig object to validate
     * @throws CommandLine.TypeConversionException if either language code is invalid
     */
    private static void validateResult(AttributeConfig attr) {
        if (!isAudioLanguageValid(attr.getAudioLanguage()))
            throw new CommandLine.TypeConversionException("Audio language invalid: " + attr.getAudioLanguage());
        if (!isLanguageValid(attr.getSubtitleLanguage()))
            throw new CommandLine.TypeConversionException("Subtitle language invalid: " + attr.getSubtitleLanguage());
    }
}

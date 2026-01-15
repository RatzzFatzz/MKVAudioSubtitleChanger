package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.converter;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import picocli.CommandLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.LanguageValidatorUtil.isLanguageValid;

public class AttributeConfigConverter implements CommandLine.ITypeConverter<AttributeConfig> {
    private static final String AUDIO_GROUP = "audio";
    private static final String SUB_GROUP = "sub";
    private static final Pattern PATTERN = Pattern.compile(String.format("^(?<%s>.{3}):(?<%s>.{3})$", AUDIO_GROUP, SUB_GROUP));

    /**
     * Converts the input string into an AttributeConfig object.
     *
     * @param s The input string containing audio and subtitle language configuration in format "audioLang:subtitleLang"
     * @return An AttributeConfig object representing the parsed configuration
     * @throws CommandLine.TypeConversionException if the input string is invalid or contains invalid language codes
     */
    @Override
    public AttributeConfig convert(String s) {
        Matcher matcher =  PATTERN.matcher(s);

        if (!matcher.find()) throw new CommandLine.TypeConversionException("Invalid Attribute config: " + s);

        return validateResult(new AttributeConfig(matcher.group(AUDIO_GROUP), matcher.group(SUB_GROUP)));
    }

    /**
     * Validates that both language codes in the {@link AttributeConfig} object are valid.
     *
     * @param attr {@link AttributeConfig} object to validate
     * @throws CommandLine.TypeConversionException if either language code is invalid
     * @return valid {@link AttributeConfig}
     */
    private static AttributeConfig validateResult(AttributeConfig attr) {
        if (!isLanguageValid(attr.getAudioLang()))
            throw new CommandLine.TypeConversionException("Audio language invalid: " + attr.getAudioLang());
        if (!isLanguageValid(attr.getSubLang()))
            throw new CommandLine.TypeConversionException("Subtitle language invalid: " + attr.getSubLang());

        return attr;
    }
}

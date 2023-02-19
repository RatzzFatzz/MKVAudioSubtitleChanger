package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternValidator extends ConfigValidator<Pattern> {
    private static final Pattern EMPTY_PATTERN = Pattern.compile("");

    public PatternValidator(ConfigProperty property, boolean required, Pattern defaultValue) {
        super(property, required, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Pattern parse(String value) {
        try {
            return Pattern.compile(value);
        } catch (PatternSyntaxException e) {
            return EMPTY_PATTERN;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isValid(Pattern result) {
        return !result.equals(EMPTY_PATTERN);
    }
}

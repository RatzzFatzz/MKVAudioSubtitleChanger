package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternValidator extends ConfigValidator<Pattern> {
    public PatternValidator(ConfigProperty property, boolean required, Pattern defaultValue) {
        super(property, required, defaultValue);
    }

    @Override
    Pattern parse(String value) {
        try {
            return Pattern.compile(value);
        } catch (PatternSyntaxException e) {
            return null;
        }
    }

    @Override
    boolean isValid(Pattern result) {
        return result != null;
    }
}

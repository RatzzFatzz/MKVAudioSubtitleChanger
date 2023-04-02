package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;

import java.util.Date;

public class DateValidator extends ConfigValidator<Date> {
    public DateValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    @Override
    Date parse(String value) {
        return DateUtils.convert(value); // TODO fix null return value
    }

    @Override
    boolean isValid(Date result) {
        return result != null;
    }
}

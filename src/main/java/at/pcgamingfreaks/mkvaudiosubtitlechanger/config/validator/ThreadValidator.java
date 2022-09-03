package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import org.apache.commons.lang3.StringUtils;

public class ThreadValidator extends ConfigValidator<Integer>{
    public ThreadValidator(ConfigProperty property, boolean required, Integer defaultValue) {
        super(property, required, defaultValue);
    }

    @Override
    Integer parse(String value) {
        return StringUtils.isNumeric(value) ? Integer.parseInt(value) : defaultValue;
    }

    @Override
    boolean isValid(Integer result) {
        return result > 0;
    }
}

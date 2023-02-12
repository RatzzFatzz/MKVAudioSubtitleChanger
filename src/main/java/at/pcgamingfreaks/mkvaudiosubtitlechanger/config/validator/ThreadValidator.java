package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import org.apache.commons.lang3.math.NumberUtils;

public class ThreadValidator extends ConfigValidator<Integer>{
    public ThreadValidator(ConfigProperty property, boolean required, Integer defaultValue) {
        super(property, required, defaultValue);
    }

    @Override
    Integer parse(String value) {
        return NumberUtils.isParsable(value) ? Integer.parseInt(value) : defaultValue;
    }

    @Override
    boolean isValid(Integer result) {
        return result > 0;
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import org.apache.commons.lang3.math.NumberUtils;

@Deprecated
public class ThreadValidator extends ConfigValidator<Integer>{
    public ThreadValidator(ConfigProperty property, boolean required, Integer defaultValue) {
        super(property, required, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Integer parse(String value) {
        return NumberUtils.isParsable(value) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isValid(Integer result) {
        return result > 0;
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import org.apache.commons.lang3.math.NumberUtils;

public class CoherentConfigValidator extends ConfigValidator<Integer> {
    private static final Integer DISABLED = -1;

    public CoherentConfigValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    @Override
    Integer parse(String value) {
        return NumberUtils.isParsable(value) ? Integer.parseInt(value) : DISABLED;
    }

    @Override
    boolean isValid(Integer result) {
        return result > 0;
    }
}

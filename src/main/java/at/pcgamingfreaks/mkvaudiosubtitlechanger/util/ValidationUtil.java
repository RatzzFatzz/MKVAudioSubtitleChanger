package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Getter;

public class ValidationUtil {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    @Getter
    private static final Validator validator = factory.getValidator();
}

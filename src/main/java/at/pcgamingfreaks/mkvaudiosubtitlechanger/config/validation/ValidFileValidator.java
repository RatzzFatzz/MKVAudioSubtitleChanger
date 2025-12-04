package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.File;

public class ValidFileValidator implements ConstraintValidator<ValidFile, File> {
    @Override
    public void initialize(ValidFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(File file, ConstraintValidatorContext context) {
        return file != null && file.exists();
    }
}

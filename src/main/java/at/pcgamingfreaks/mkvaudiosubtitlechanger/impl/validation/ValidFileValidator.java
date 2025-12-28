package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.File;
import java.util.Arrays;

public class ValidFileValidator implements ConstraintValidator<ValidFile, File[]> {
    @Override
    public void initialize(ValidFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(File[] files, ConstraintValidatorContext context) {
        return files != null && files.length > 0 && Arrays.stream(files).allMatch(File::exists);
    }
}

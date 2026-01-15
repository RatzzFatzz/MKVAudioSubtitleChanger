package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;

@Slf4j
public class ValidFileValidator implements ConstraintValidator<ValidFile, File[]> {
    @Override
    public void initialize(ValidFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(File[] files, ConstraintValidatorContext context) {
        if (files == null || files.length == 0) return false;
        for (File file: files) {
            if (!file.exists()) {
                log.error("{} does not exist", file.getPath());
                return false;
            }
        }
        return true;
    }
}

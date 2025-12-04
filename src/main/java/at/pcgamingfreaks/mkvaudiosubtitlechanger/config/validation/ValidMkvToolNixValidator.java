package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.io.File;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.FileUtils.getPathFor;

public class ValidMkvToolNixValidator implements ConstraintValidator<ValidMkvToolNix, File> {
    @Override
    public void initialize(ValidMkvToolNix constraintAnnotation) {
    }

    @Override
    public boolean isValid(File file, ConstraintValidatorContext context) {
        return file != null && file.exists()
                && getPathFor(file, MkvToolNix.MKV_MERGE).exists()
                && getPathFor(file, MkvToolNix.MKV_PROP_EDIT).exists();
    }


}

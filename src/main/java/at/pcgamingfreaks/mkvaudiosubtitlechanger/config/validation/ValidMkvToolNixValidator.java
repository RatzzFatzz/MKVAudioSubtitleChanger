package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validation;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.SystemUtils;


import java.io.File;
import java.nio.file.Path;

public class ValidMkvToolNixValidator implements ConstraintValidator<ValidMkvToolNix, File> {
    @Override
    public void initialize(ValidMkvToolNix constraintAnnotation) {
    }

    @Override
    public boolean isValid(File file, ConstraintValidatorContext context) {
        return file != null && file.exists()
                && Path.of(InputConfig.getInstance().getPathFor(MkvToolNix.MKV_MERGE, SystemUtils.IS_OS_WINDOWS)).toFile().exists()
                && Path.of(InputConfig.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT, SystemUtils.IS_OS_WINDOWS)).toFile().exists();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;

import java.io.File;
import java.nio.file.Path;

public class PathValidator extends ConfigValidator<File> {

    public PathValidator(ConfigProperty property, boolean required, File defaultValue) {
        super(property, required, defaultValue);
    }

    @Override
    protected File parse(String value) {
        return Path.of(value).toFile();
    }

    @Override
    protected boolean isValid(File result) {
        return result.isDirectory() || result.isFile();
    }

}

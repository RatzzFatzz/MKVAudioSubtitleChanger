package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;

import java.io.File;
import java.nio.file.Path;

@Deprecated
public class PathValidator extends ConfigValidator<File> {

    public PathValidator(ConfigProperty property, boolean required, File defaultValue) {
        super(property, required, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected File parse(String value) {
        return Path.of(value).toFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValid(File result) {
        return result.isDirectory() || result.isFile();
    }

}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;

import java.io.File;
import java.util.Optional;
import java.util.function.BiFunction;

public class ConfigPathValidator extends PathValidator {
    public ConfigPathValidator(ConfigProperty property, boolean required, File defaultValue) {
        super(property, required, defaultValue);
    }

    @Override
    protected BiFunction<YAML, ConfigProperty, Optional<File>> provideDataYaml() {
        return (yaml, property) -> Optional.empty();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;

import java.io.File;
import java.util.Optional;
import java.util.function.BiFunction;

@Deprecated
public class ConfigPathValidator extends PathValidator {
    public ConfigPathValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BiFunction<YAML, ConfigProperty, Optional<File>> provideDataYaml() {
        return (yaml, property) -> Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValid(File result) {
        return super.isValid(result) && (result.getAbsolutePath().endsWith(".yml") || result.getAbsolutePath().endsWith(".yaml"));
    }

    @Override
    public int getWeight() {
        return 100;
    }
}

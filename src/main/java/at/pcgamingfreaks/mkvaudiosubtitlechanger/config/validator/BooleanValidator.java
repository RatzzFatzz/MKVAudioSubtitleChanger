package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import org.apache.commons.cli.CommandLine;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.ARGUMENTS;

public class BooleanValidator extends ConfigValidator<Boolean> {

    public BooleanValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    /**
     * {@inheritDoc}
     */
    protected BiFunction<YAML, ConfigProperty, Optional<Boolean>> provideDataYaml() {
        return (yaml, property) -> {
            if (yaml.isSet(ARGUMENTS.prop())
                    && yaml.getStringList(ARGUMENTS.prop(), List.of()).contains(property.prop())) {
                return Optional.of(true);
            }
            return Optional.empty();
        };
    }

    /**
     * {@inheritDoc}
     */
    protected BiFunction<CommandLine, ConfigProperty, Optional<Boolean>> provideDataCmd() {
        return (cmd, property) -> {
            if (cmd.hasOption(property.prop())) {
                return  Optional.of(true);
            }
            return Optional.empty();
        };
    }

    /**
     * {@inheritDoc}
     * This should not be used.
     */
    @Override
    Boolean parse(String value) {
        throw new RuntimeException("This should not be called");
    }

    /**
     * {@inheritDoc}
     * Validation is skipped.
     */
    @Override
    boolean isValid(Boolean result) {
        return true; // skip
    }
}

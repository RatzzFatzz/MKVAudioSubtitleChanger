package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import org.apache.commons.cli.CommandLine;

import java.util.List;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.ARGUMENTS;

public class BooleanValidator extends ConfigValidator<Boolean> {

    public BooleanValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    public ValidationResult validate(YAML yaml, CommandLine cmd) {
        System.out.printf("Checking %s... ", property.prop());
        boolean result;

        if (cmd.hasOption(property.prop())) {
            result = true;
        } else if (yaml.isSet(ARGUMENTS.prop())
                && yaml.getStringList(ARGUMENTS.prop(), List.of()).contains(property.prop())) {
            result = true;
        } else if (required) {
            System.out.println("missing");
            return ValidationResult.MISSING;
        } else {
            System.out.println("ok");
            return ValidationResult.NOT_PRESENT;
        }

        if (!isValid(result) || !setValue(result)) {
            System.out.println("invalid");
            return ValidationResult.INVALID;
        }

        System.out.println("ok");
        return ValidationResult.VALID;
    }

    @Override
    Boolean parse(String value) {
        throw new RuntimeException("This should not be called");
    }

    @Override
    boolean isValid(Boolean result) {
        return true; // skip
    }
}

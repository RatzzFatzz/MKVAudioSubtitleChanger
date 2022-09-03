package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;

public class OperatingSystemValidator extends BooleanValidator {

    public OperatingSystemValidator(ConfigProperty property) {
        super(property, false);
    }

    @Override
    public ValidationResult validate(YAML yaml, CommandLine cmd) {
        System.out.printf("Checking %s... ", property.prop());
        Boolean result = StringUtils.containsIgnoreCase(System.getProperty("os.name"), "windows");

        if (!isValid(result) || !setValue(result)) {
            System.out.println("invalid");
            return ValidationResult.INVALID;
        }

        System.out.println("ok");
        return ValidationResult.VALID;
    }
}

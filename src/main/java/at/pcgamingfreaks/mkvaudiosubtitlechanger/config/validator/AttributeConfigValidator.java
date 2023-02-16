package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import org.apache.commons.cli.CommandLine;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.LanguageValidatorUtil.isLanguageValid;

public class AttributeConfigValidator extends ConfigValidator<List<AttributeConfig>> {

    public AttributeConfigValidator() {
        super(ConfigProperty.ATTRIBUTE_CONFIG, true, null);
    }

    public ValidationResult validate(YAML yaml, CommandLine cmd) {
        System.out.printf("%s: ", property.prop());
        List<AttributeConfig> result;

        Function<String, String> audio = key -> yaml.getString(key + ".audio", null);
        Function<String, String> subtitle = key -> yaml.getString(key + ".subtitle", null);

        if (yaml.getKeysFiltered(property.prop() + ".*").size() > 0) {
            result = yaml.getKeysFiltered(".*audio.*").stream()
                    .sorted()
                    .map(key -> key.replace(".audio", ""))
                    .map(key -> new AttributeConfig(audio.apply(key), subtitle.apply(key)))
                    .collect(Collectors.toList());
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
    List<AttributeConfig> parse(String value) {
        return null;
    }

    @Override
    boolean isValid(List<AttributeConfig> result) {
        if (result.isEmpty()) {
            return false;
        }
        boolean isValid = true;
        for (AttributeConfig attributeConfig : result) {
            isValid = isLanguageValid(attributeConfig.getAudioLanguage())
                            && isLanguageValid(attributeConfig.getSubtitleLanguage());
            if (!isValid) return false;
        }
        return true;
    }
}

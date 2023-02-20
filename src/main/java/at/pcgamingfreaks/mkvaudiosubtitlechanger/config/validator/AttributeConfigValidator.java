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
    private static final String SEPARATOR = ":";

    public AttributeConfigValidator() {
        super(ConfigProperty.ATTRIBUTE_CONFIG, true, null);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(YAML yaml, CommandLine cmd) {
        System.out.printf("%s: ", property.prop());
        List<AttributeConfig> result;


        if (cmd.hasOption(property.prop())) {
            List<String> values = List.of(cmd.getOptionValues(property.prop()));
            result = values.stream().anyMatch(pair -> !pair.contains(SEPARATOR))
                    ? List.of()
                    : values.stream().map(pair -> pair.split(SEPARATOR))
                    .map(split -> new AttributeConfig(split[0], split[1]))
                    .collect(Collectors.toList());
        } else if(yaml.getKeysFiltered(property.prop() + ".*").size() > 0) {
            Function<String, String> audio = key -> yaml.getString(key + ".audio", null);
            Function<String, String> subtitle = key -> yaml.getString(key + ".subtitle", null);

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

    /**
     * {@inheritDoc}
     */
    @Override
    List<AttributeConfig> parse(String value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isValid(List<AttributeConfig> result) {
        if (result.isEmpty()) {
            return false;
        }
        boolean isValid;
        for (AttributeConfig attributeConfig : result) {
            isValid = isLanguageValid(attributeConfig.getAudioLanguage())
                            && isLanguageValid(attributeConfig.getSubtitleLanguage());
            if (!isValid) return false;
        }
        return true;
    }
}

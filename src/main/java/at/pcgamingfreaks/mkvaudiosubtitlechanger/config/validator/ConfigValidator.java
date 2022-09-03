package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ConfigValidator<FieldType> {
	protected final ConfigProperty property;
	protected final boolean required;
	protected final FieldType defaultValue;

    public ValidationResult validate(YAML yaml, CommandLine cmd) {
		System.out.printf("Checking %s... ", property.prop());
		String resultString = null;

		if (cmd.hasOption(property.prop())) {
			resultString = cmd.getOptionValue(property.prop());
		} else if (yaml.isSet(property.prop())) {
			try {
				resultString = yaml.getString(property.prop());
			} catch (YamlKeyNotFoundException ignored) {}
		} else if (required) {
			System.out.println("missing");
			return ValidationResult.MISSING;
		} else {
			System.out.println("ok");
			return ValidationResult.NOT_PRESENT;
		}

		FieldType result = parse(resultString);

		if (!isValid(result) || !setValue(result)) {
			System.out.println("invalid");
			return ValidationResult.INVALID;
		}

		System.out.println("ok");
		return ValidationResult.VALID;
	}

	abstract FieldType parse(String value);

	abstract boolean isValid(FieldType result);

	protected boolean setValue(FieldType result) {
        List<Method> methods = Arrays.stream(Config.getInstance().getClass().getDeclaredMethods())
                .filter(containsSetterOf(property))
                .collect(Collectors.toList());
        if (methods.size() != 1) {
            return false;
        }
        try {
			methods.get(0).invoke(Config.getInstance(), result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
		return true;
    }

	protected Predicate<Method> containsSetterOf(ConfigProperty property) {
		return method -> StringUtils.containsIgnoreCase(method.getName(), "set")
				&& StringUtils.containsIgnoreCase(method.getName(), property.prop().replace("-", ""));
	}

	protected Predicate<Method> containsGetterOf(ConfigProperty property) {
		return method -> StringUtils.containsIgnoreCase(method.getName(), "get")
				&& StringUtils.containsIgnoreCase(method.getName(), property.prop().replace("-", ""));
	}


}

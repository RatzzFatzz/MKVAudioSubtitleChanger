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
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ConfigValidator<FieldType> {
	protected final ConfigProperty property;
	protected final boolean required;
	protected final FieldType defaultValue;

	/**
	 * Validate the user input. Parameters of cmd are prioritised.
	 *
	 * @param yaml config file
	 * @param cmd command line parameters
	 * @return {@link ValidationResult} containing validity of input.
	 */
    public ValidationResult validate(YAML yaml, CommandLine cmd) {
		System.out.printf("%s: ", property.prop());
		FieldType result;

		Optional<FieldType> cmdResult = provideDataCmd().apply(cmd, property);
		Optional<FieldType> yamlResult = provideDataYaml().apply(yaml, property);

		if (cmdResult.isPresent()) {
			result = cmdResult.get();
		} else if (yamlResult.isPresent()) {
			result = yamlResult.get();
		} else {
			if (defaultValue != null) {
				if (setValue(defaultValue)) {
					System.out.println("default");
					return ValidationResult.DEFAULT;
				} else {
					System.out.println("invalid");
					return ValidationResult.INVALID;
				}
			}
			if (required) {
				System.out.println("missing");
				return ValidationResult.MISSING;
			} else {
				System.out.println("ok");
				return ValidationResult.NOT_PRESENT;
			}
		}

		if (!isValid(result) || !setValue(result)) {
			System.out.println("invalid");
			return ValidationResult.INVALID;
		}

		System.out.println("ok");
		return ValidationResult.VALID;
	}

	/**
	 * @return parsed input of yaml config for property
	 */
	protected BiFunction<YAML, ConfigProperty, Optional<FieldType>> provideDataYaml() {
		return (yaml, property) -> {
			if (yaml.isSet(property.prop())) {
				try {
					return  Optional.of(parse(yaml.getString(property.prop())));
				} catch (YamlKeyNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			return Optional.empty();
		};
	}

	/**
	 * @return parsed input of command line parameters config for property
	 */
	protected BiFunction<CommandLine, ConfigProperty, Optional<FieldType>> provideDataCmd() {
		return (cmd, property) -> {
			if (cmd.hasOption(property.prop())) {
				return  Optional.of(parse(cmd.getOptionValue(property.prop())));
			}
			return Optional.empty();
		};
	}

	/**
	 * Parse input parameter to desired format.
	 *
	 * @param value input parameter
	 * @return parsed property
	 */
	abstract FieldType parse(String value);

	/**
	 * Validate if the data has the desired and allowed format.
	 *
	 * @param result parsed property
	 * @return true if data is in desired format.
	 */
	abstract boolean isValid(FieldType result);

	/**
	 * Sets valid properties to {@link Config} via reflections.
	 *
	 * @param result parsed property
	 * @return false if method invocation failed
	 */
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

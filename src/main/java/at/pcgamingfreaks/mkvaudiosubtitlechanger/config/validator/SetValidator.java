package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import org.apache.commons.cli.CommandLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SetValidator extends ConfigValidator<Set<String>> {
    private final boolean append;

    public SetValidator(ConfigProperty property, boolean required, boolean append) {
        super(property, required, null);
        this.append = append;
    }

    public ValidationResult validate(YAML yaml, CommandLine cmd) {
        System.out.printf("Checking %s... ", property.prop());
        List<String> resultList = null;

        if (cmd.hasOption(property.prop())) {
            resultList = List.of(cmd.getOptionValues(property.prop()));
        } else if (yaml.isSet(property.prop())) {
            try {
                resultList = yaml.getStringList(property.prop());
            } catch (YamlKeyNotFoundException ignored) {}
        } else if (required) {
            System.out.println("missing");
            return ValidationResult.MISSING;
        } else {
            System.out.println("not present");
            return ValidationResult.NOT_PRESENT;
        }

        Set<String> result = parse(resultList);

        if (!isValid(result) || !setValue(result)) {
            System.out.println("invalid");
            return ValidationResult.INVALID;
        }

        System.out.println("ok");
        return ValidationResult.VALID;
    }
    @Override
    Set<String> parse(String value) {
        throw new RuntimeException("This should not be called");
    }

    protected Set<String> parse(List<String> value) {
        return new HashSet<>(value);
    }

    @Override
    boolean isValid(Set<String> result) {
        return true;
    }

    protected boolean setValue(Set<String> result) {
        List<Method> methods = append
                ? Arrays.stream(Config.getInstance().getClass().getDeclaredMethods())
                    .filter(containsGetterOf(property))
                    .collect(Collectors.toList())
                : Arrays.stream(Config.getInstance().getClass().getDeclaredMethods())
                    .filter(containsSetterOf(property))
                    .collect(Collectors.toList());
        if (methods.size() != 1) {
            return false;
        }
        try {
            if (append) {
                ((Set<String>) methods.get(0).invoke(Config.getInstance())).addAll(result);
            } else {
                methods.get(0).invoke(Config.getInstance(), result);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigErrors {
    private final List<String> errors = new ArrayList<>();

    public void add(String errorMessage) {
        errors.add(errorMessage);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String toString() {
        return StringUtils.capitalize(String.join(", ", errors));
    }
}

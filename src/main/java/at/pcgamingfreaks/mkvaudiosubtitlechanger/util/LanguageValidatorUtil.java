package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ConfigErrors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LanguageValidatorUtil {
    private static Set<String> ISO3_LANGUAGES;

    static {
        try {
            ISO3_LANGUAGES = load("language-codes");
        } catch (IOException ignored) {}
    }

    private static Set<String> load(String path) throws IOException{
        if (new File(path).isFile()) {
            return Files.lines(Path.of(path)).collect(Collectors.toSet());
        } else {
            try(BufferedReader bf = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(LanguageValidatorUtil.class.getClassLoader().getResourceAsStream(path))))) {
                return bf.lines().collect(Collectors.toSet());
            }
        }
    }

    public static boolean isLanguageValid(String language) {
        return ISO3_LANGUAGES.contains(language);
    }

    public static void isLanguageValid(String language, ConfigErrors errors) {
        if (!isLanguageValid(language)) {
            errors.add(String.format("%s is not a valid language", language));
        }
    }
}

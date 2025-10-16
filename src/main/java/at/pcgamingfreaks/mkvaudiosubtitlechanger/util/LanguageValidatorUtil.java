package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LanguageValidatorUtil {
    private static Set<String> ISO3_LANGUAGES;

    static {
        try {
            ISO3_LANGUAGES = loadLanguageCodes();
        } catch (IOException ignored) {}
    }

    private static Set<String> loadLanguageCodes() throws IOException {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(LanguageValidatorUtil.class.getClassLoader().getResourceAsStream("language-codes"))))) {
            return bf.lines().collect(Collectors.toSet());
        }
    }

    public static boolean isLanguageValid(String language) {
        return ISO3_LANGUAGES.contains(language);
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty.MKV_TOOL_NIX;

@Log4j2
@Getter
@Setter
public class Config {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Config config = null;

    private List<AttributeConfig> attributeConfig;
    private int threadCount;
    private Set<String> forcedKeywords = new HashSet<>(Arrays.asList("forced", "signs"));
    private Set<String> excludedDirectories = new HashSet<>();
    @Getter(AccessLevel.NONE)
    private String mkvtoolnixPath;
    private String libraryPath;
    private boolean isSafeMode;
    private boolean isWindows;

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public void isValid() throws RuntimeException {
        boolean isValid = true;
        if (attributeConfig == null || attributeConfig.isEmpty()
                || !attributeConfig.stream().allMatch(AttributeConfig::isValid)) {
            isValid = false;
            System.out.println("Audio & subtitle configuration invalid!");
        }
        if (threadCount <= 0) {
            isValid = false;
            System.out.println("Thread count needs to be at least 1!");
        }
        if (mkvtoolnixPath.isEmpty()
                || !new File(getPathFor(MkvToolNix.MKV_MERGER)).isFile()
                || !new File(getPathFor(MkvToolNix.MKV_PROP_EDIT)).isFile()) {
            isValid = false;
            System.out.println("MkvToolNix installation path invalid!");
        }

        if (!isValid) {
            throw new RuntimeException("Invalid configuration");
        }
    }

    public void loadConfig(String configPath) {
        try (YAML config = new YAML(new File(configPath))) {
            setAttributeConfig(loadAttributeConfig(config));
            setThreadCount(loadThreadCount(config));
            setMkvtoolnixPath(loadMkvToolNixPath(config));
            setWindows(System.getProperty("os.name").toLowerCase().contains("windows"));
            getForcedKeywords().addAll(loadForcedKeywords(config));
            getExcludedDirectories().addAll(loadExcludeDirectories(config));
        } catch (YamlInvalidContentException | YamlKeyNotFoundException | IOException e) {
            log.fatal("Config could not be loaded: {}", e.getMessage());
        }
    }

    private List<AttributeConfig> loadAttributeConfig(YAML config) {
        Function<String, String> audio = key -> config.getString(key + ".audio", null);
        Function<String, String> subtitle = key -> config.getString(key + ".subtitle", null);

        return config.getKeysFiltered(".*audio.*").stream()
                .sorted()
                .map(key -> key.replace(".audio", ""))
                .map(key -> new AttributeConfig(audio.apply(key), subtitle.apply(key)))
                .collect(Collectors.toList());
    }

    private int loadThreadCount(YAML config) throws YamlKeyNotFoundException {
        return config.isSet(ConfigProperty.THREADS.prop())
                ? Integer.parseInt(config.getString(ConfigProperty.THREADS.prop()))
                : 1;
    }

    private List<String> loadForcedKeywords(YAML config) {
        return config.getStringList(ConfigProperty.FORCED_KEYWORDS.prop(), new ArrayList<>());
    }

    private List<String> loadExcludeDirectories(YAML config) {
        return config.getStringList(ConfigProperty.EXCLUDE_DIRECTORY.prop(), new ArrayList<>());
    }

    private String loadMkvToolNixPath(YAML config) throws YamlKeyNotFoundException {
        return config.isSet(MKV_TOOL_NIX.prop())
                ? config.getString(MKV_TOOL_NIX.prop())
                : defaultMkvToolNixPath();
    }

    private String defaultMkvToolNixPath() {
        return System.getProperty("os.name").toLowerCase().contains("windows")
                ? "C:/Program Files/MKVToolNix/"
                : "/usr/bin/";
    }

    public String getPathFor(MkvToolNix exe) {
        return mkvtoolnixPath.endsWith("/") ? mkvtoolnixPath + exe : mkvtoolnixPath + "/" + exe;
    }
}


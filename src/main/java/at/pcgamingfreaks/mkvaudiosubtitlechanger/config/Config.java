package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
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
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Setter
public class Config {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Config config = null;

    private List<AttributeConfig> attributeConfig;
    private int threadCount;
    @Getter(AccessLevel.NONE)
    private String mkvtoolnixPath;
    private String libraryPath;
    private boolean isSafeMode;
    private boolean isWindows;

    public static Config getInstance() {
        if(config == null) {
            config = new Config();
        }
        return config;
    }

    public void isValid() throws RuntimeException{
        System.out.println(attributeConfig);
        System.out.println(threadCount);
        System.out.println(mkvtoolnixPath);
        System.out.println(libraryPath);
        if (attributeConfig != null && !attributeConfig.isEmpty()
                && threadCount > 0 && !mkvtoolnixPath.isEmpty()
                && new File(getPathFor(MkvToolNix.MKV_MERGER)).isFile()
                && new File(getPathFor(MkvToolNix.MKV_PROP_EDIT)).isFile()) {
            return;
        }
        throw new RuntimeException("Invalid configuration");
    }

    public void loadConfig(String configPath) {
        try(YAML config = new YAML(new File(configPath))){
            setAttributeConfig(loadAttributeConfig(config));
            setThreadCount(loadThreadCount(config));
            setMkvtoolnixPath(loadMkvToolNixPath(config));
            setWindows(System.getProperty("os.name").toLowerCase().contains("windows"));
        }catch(YamlInvalidContentException | YamlKeyNotFoundException | IOException e){
            log.fatal("Config could not be loaded: {}", e.getMessage());
        }
    }

    private List<AttributeConfig> loadAttributeConfig(YAML config) {
        return config.getKeysFiltered(".*audio.*").stream()
                .sorted()
                .map(elem -> elem.replace(".audio", ""))
                .map(elem -> createAttributeConfig(elem, config))
                .collect(Collectors.toList());
    }

    private AttributeConfig createAttributeConfig(String key, YAML config) {
        try{
            return new AttributeConfig(
                    config.getStringList(key + ".audio"),
                    config.getStringList(key + ".subtitle"));
        }catch(YamlKeyNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    private int loadThreadCount(YAML config) throws YamlKeyNotFoundException{
        return config.isSet("threadCount")
                ? Integer.parseInt(config.getString("threadCount"))
                : 1;
    }

    private String loadMkvToolNixPath(YAML config) throws YamlKeyNotFoundException {
        return config.isSet("mkvtoolnixPath") ? config.getString("mkvtoolnixPath") : defaultMkvToolNixPath();
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


package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.AttributeConfig;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ConfigUtil {
    public static List<AttributeConfig> loadConfig() {
        try(YAML yaml = new YAML(new File("config.yaml"))){
            return yaml.getKeysFiltered(".*audio.*").stream()
                    .sorted()
                    .map(elem -> elem.replace(".audio", ""))
                    .map(elem -> createAttributeConfig(elem, yaml))
                    .collect(Collectors.toList());
        }catch(YamlInvalidContentException | IOException e){
            log.fatal("Config could not be loaded");
            e.printStackTrace();
        }
        return null;
    }

    private static AttributeConfig createAttributeConfig(String key, YAML yaml) {
        try{
            return new AttributeConfig(
                    yaml.getStringList(key + ".audio"),
                    yaml.getStringList(key + ".subtitle"));
        }catch(YamlKeyNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public static int getThreadCount() {
        try {
            return Integer.parseInt(new YAML(new File("config.yaml")).getString("threadCount"));
        } catch (YamlInvalidContentException | IOException e) {
            e.printStackTrace();
        } catch (YamlKeyNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}

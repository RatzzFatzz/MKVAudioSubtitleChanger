package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;

@Log4j2
public class Main {
    public static void main(String[] args) {
        if(checkIfMKVToolNixIsValid()){
            AttributeUpdaterKernel kernel = new AttributeUpdaterKernel();
            kernel.execute(args[0]);
        }else{
            log.error("MKVToolNix was not found! Please recheck path");
        }
    }

    private static boolean checkIfMKVToolNixIsValid() {
        try{
            String path = new YAML(new File("config.yaml")).getString("mkvtoolnixPath");
            if(! path.endsWith(File.separator)){
                path += File.separator;
            }
            if(System.getProperty("os.name").toLowerCase().contains("windows")){
                MKVToolProperties.getInstance().setMkvmergePath(path + "mkvmerge.exe");
                MKVToolProperties.getInstance().setMkvpropeditPath(path + "mkvpropedit.exe");
            }else{
                MKVToolProperties.getInstance().setMkvmergePath(path + "mkvmerge");
                MKVToolProperties.getInstance().setMkvpropeditPath(path + "mkvpropedit");
            }
        }catch(YamlKeyNotFoundException | IOException | YamlInvalidContentException e){
            e.printStackTrace();
        }
        return new File(MKVToolProperties.getInstance().getMkvmergePath()).isFile() && new File(MKVToolProperties.getInstance().getMkvpropeditPath()).isFile();
    }
}

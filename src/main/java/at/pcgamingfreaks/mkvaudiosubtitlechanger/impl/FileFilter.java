package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class FileFilter {
    static boolean accept(File pathName, String[] fileExtensions) {
        return StringUtils.endsWithAny(pathName.getAbsolutePath().toLowerCase(), fileExtensions)
                && Config.getInstance().getIncludePattern().matcher(pathName.getName()).matches();
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

@Slf4j
public class FileFilter {
    static boolean accept(File pathName, String[] fileExtensions) {
        if (hasProperFileExtension(pathName, fileExtensions)
                && hasMatchingPattern(pathName)
                && isNewer(pathName)) {
            return true;
        }

        ResultStatistic.getInstance().total();
        ResultStatistic.getInstance().excluded();
        return false;
    }

    private static boolean hasProperFileExtension(File pathName, String[] fileExtensions) {
        return StringUtils.endsWithAny(pathName.getAbsolutePath().toLowerCase(), fileExtensions);
    }

    private static boolean hasMatchingPattern(File pathName) {
        return Config.getInstance().getIncludePattern().matcher(pathName.getName()).matches();
    }

    private static boolean isNewer(File pathName) {
        Config config = Config.getInstance();
        if (config.getFilterDate() == null) return true;
        try {
            BasicFileAttributes attributes = Files.readAttributes(pathName.toPath(), BasicFileAttributes.class);
            return isNewer(DateUtils.convert(attributes.creationTime().toMillis()));
        } catch (IOException e) {
            log.warn("File attributes could not be read.", e);
        }
        return true;
    }

    private static boolean isNewer(Date creationDate) {
        return creationDate.toInstant().isAfter(Config.getInstance().getFilterDate().toInstant());
    }
}

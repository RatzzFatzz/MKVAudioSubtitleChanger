package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Path;

public class FileUtils {
    private static final boolean isWindows = SystemUtils.IS_OS_WINDOWS;

    private static String expandPath(File dir, MkvToolNix application) {
        return dir.getAbsolutePath().endsWith("/")
                ? dir.getAbsolutePath() + application
                : dir.getAbsolutePath() + "/" + application;
    }

    public static File getPathFor(File dir, MkvToolNix application) {
        return Path.of(expandPath(dir, application) + (isWindows ? ".exe" : "")).toFile();
    }
}

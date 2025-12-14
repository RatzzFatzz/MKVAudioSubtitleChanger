package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import org.apache.commons.lang3.SystemUtils;

public class PathUtils {
    public static final String TEST_DIR = "src/test/resources/test-dir";
    public static final String TEST_FILE = "src/test/resources/test-dir/test-file.mkv";
    public static final String TEST_INVALID_DIR = "src/test/resources/test-dir";
    public static final String TEST_MKVTOOLNIX_DIR = SystemUtils.IS_OS_WINDOWS ? "src/test/resources/mkvtoolnix_exe" : "src/test/resources/mkvtoolnix";
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

class ConfigTest {
    private static final File TEST_MKVTOOLNIX_DIR = Path.of("src/test/resources/mkvtoolnix").toFile();
    private static final File TEST_MKVTOOLNIX_EXE_DIR = Path.of("src/test/resources/mkvtoolnix_exe").toFile();

    @Test
    void getPathForWindows() {
        Config.getInstance().setWindows(true);
        Config.getInstance().setMkvToolNix(TEST_MKVTOOLNIX_EXE_DIR);

        assert Config.getInstance().getPathFor(MkvToolNix.MKV_MERGER).endsWith(MkvToolNix.MKV_MERGER + ".exe");
        assert Config.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT).endsWith(MkvToolNix.MKV_PROP_EDIT + ".exe");
    }

    @Test
    void getPathForUnix() {
        Config.getInstance().setWindows(false);
        Config.getInstance().setMkvToolNix(TEST_MKVTOOLNIX_DIR);

        assert Config.getInstance().getPathFor(MkvToolNix.MKV_MERGER).endsWith(MkvToolNix.MKV_MERGER.toString());
        assert Config.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT).endsWith(MkvToolNix.MKV_PROP_EDIT.toString());
    }
}
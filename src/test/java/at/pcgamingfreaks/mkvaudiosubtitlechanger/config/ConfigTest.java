package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.List;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void initConfig() {
        String[] sut = new String[]{"-a", "ger:ger", "eng:eng", "-l", TEST_FILE,
                "-s", "-cf", "-n",
                "-c", "2",
                "-t", "4",
                "-i", ".*[abc].*",
                "--forced-keywords", "testForced",
                "--commentary-keywords", "testCommentary",
                "--preferred-subtitles", "testPreferred"
        };
        CommandLine.populateCommand(Config.getInstance(), sut);

        assertTrue(Config.getInstance().getLibraryPath().exists());
        assertEquals(List.of(new AttributeConfig("ger", "ger"), new AttributeConfig("eng", "eng")),
                Config.getInstance().getAttributeConfig());

        assertTrue(Config.getInstance().isSafeMode());
        assertTrue(Config.getInstance().isForceCoherent());
        assertTrue(Config.getInstance().isOnlyNewFiles());
        assertNull(Config.getInstance().getFilterDate());

        assertEquals(2, Config.getInstance().getCoherent());
        assertEquals(4, Config.getInstance().getThreads());
        assertEquals(".*[abc].*", Config.getInstance().getIncludePattern().pattern());
        assertTrue(Config.getInstance().getForcedKeywords().contains("testForced"));
        assertTrue(Config.getInstance().getCommentaryKeywords().contains("testCommentary"));
        assertTrue(Config.getInstance().getPreferredSubtitles().contains("testPreferred"));

        assertNull(Config.getInstance().getConfigPath());

    }
}
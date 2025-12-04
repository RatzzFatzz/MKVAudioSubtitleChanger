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
        CommandLine.populateCommand(InputConfig.getInstance(true), sut);

        assertTrue(InputConfig.getInstance().getLibraryPath().exists());
        assertEquals(List.of(new AttributeConfig("ger", "ger"), new AttributeConfig("eng", "eng")),
                InputConfig.getInstance().getAttributeConfig());

        assertTrue(InputConfig.getInstance().isSafeMode());
        assertTrue(InputConfig.getInstance().isForceCoherent());
        assertTrue(InputConfig.getInstance().isOnlyNewFiles());
        assertNull(InputConfig.getInstance().getFilterDate());

        assertEquals(2, InputConfig.getInstance().getCoherent());
        assertEquals(4, InputConfig.getInstance().getThreads());
        assertEquals(".*[abc].*", InputConfig.getInstance().getIncludePattern().pattern());
        assertTrue(InputConfig.getInstance().getForcedKeywords().contains("testForced"));
        assertTrue(InputConfig.getInstance().getCommentaryKeywords().contains("testCommentary"));
        assertTrue(InputConfig.getInstance().getPreferredSubtitles().contains("testPreferred"));

        assertNull(InputConfig.getInstance().getConfigPath());
    }
}
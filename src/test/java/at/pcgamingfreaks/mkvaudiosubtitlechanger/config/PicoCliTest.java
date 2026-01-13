package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CommandRunner;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation.ValidationExecutionStrategy;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.time.Instant;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.PathUtils.TEST_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PicoCliTest {

    @Test
    void loadFilterDate() {
        CommandRunner underTest = new CommandRunner();
        new CommandLine(underTest)
                .setExecutionStrategy(new ValidationExecutionStrategy())
                .parseArgs("-d", "2012-12-12T12:12:12.00Z", TEST_FILE);
        assertEquals(Instant.parse("2012-12-12T12:12:12.00Z"), underTest.getConfig().getFilterDate());
    }
}

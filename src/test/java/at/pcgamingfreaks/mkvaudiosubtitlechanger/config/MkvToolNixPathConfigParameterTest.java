package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.Main;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.CommandLineOptionsUtil.optionOf;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.util.TestUtil.args;
import static org.junit.jupiter.api.Assertions.*;

class MkvToolNixPathConfigParameterTest {

    @Test
    void validate() {
        Main sut = new Main();
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-m", "./")));
        assertThrows(CommandLine.ParameterException.class, () -> CommandLine.populateCommand(sut, args("-m")));
    }
}
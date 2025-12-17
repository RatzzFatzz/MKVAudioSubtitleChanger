package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class MkvFileProcessorTest {

    @Test
    void readAttributes() throws IOException {
        String mkvmergeResponse = """
                {
                  "tracks": [
                    {
                      "id": 0,
                      "properties": {
                        "default_track": true,
                        "enabled_track": true,
                        "forced_track": false,
                        "language": "jpn",
                        "number": 1
                      },
                      "type": "video"
                    },
                    {
                      "id": 1,
                      "properties": {
                        "track_name": "testing",
                        "default_track": true,
                        "enabled_track": true,
                        "forced_track": false,
                        "language": "jpn",
                        "number": 2
                      },
                      "type": "audio"
                    },
                    {
                      "id": 2,
                      "properties": {
                        "default_track": true,
                        "enabled_track": true,
                        "forced_track": false,
                        "commentary_track": true,
                        "flag_hearing_impaired": true,
                        "language": "eng",
                        "number": 3
                      },
                      "type": "subtitles"
                    }
                  ]
                }
                """;

        MkvFileProcessor underTest = spy(new MkvFileProcessor(new File("mkvtoolnix"), null));
        doReturn(new ByteArrayInputStream(mkvmergeResponse.getBytes(StandardCharsets.UTF_8)))
                .when(underTest).run(any(String[].class));

        FileInfo result = underTest.readAttributes(new File("arst"));

        TrackAttributes audio = result.getAudioTracks().get(0);
        assertEquals(2, audio.id());
        assertEquals("testing", audio.trackName());
        assertEquals("jpn", audio.language());
        assertTrue(audio.defaultt());
        assertFalse(audio.forced());
        assertFalse(audio.hearingImpaired());
        assertFalse(audio.commentary());
        assertEquals(TrackType.AUDIO, audio.type());

        TrackAttributes sub = result.getSubtitleTracks().get(0);
        assertEquals(3, sub.id());
        assertNull(sub.trackName());
        assertEquals("eng", sub.language());
        assertTrue(sub.defaultt());
        assertFalse(sub.forced());
        assertTrue(sub.hearingImpaired());
        assertTrue(sub.commentary());
        assertEquals(TrackType.SUBTITLES, sub.type());
    }

    @Test
    void getUpdateCommand() throws InvocationTargetException, IllegalAccessException {
        FileInfo fileInfo = new FileInfo(new File("./"));
        fileInfo.getChanges().getDefaultTrack().put(t(1), true);
        fileInfo.getChanges().getDefaultTrack().put(t(2), false);
        fileInfo.getChanges().getForcedTrack().put(t(3), true);
        fileInfo.getChanges().getForcedTrack().put(t(4), false);
        fileInfo.getChanges().getCommentaryTrack().put(t(5), true);
        fileInfo.getChanges().getCommentaryTrack().put(t(6), false);
        fileInfo.getChanges().getHearingImpairedTrack().put(t(7), true);
        fileInfo.getChanges().getHearingImpairedTrack().put(t(8), false);
        String[] expectedCommand = """
                --edit track:1 --set flag-default=1
                --edit track:2 --set flag-default=0
                --edit track:3 --set flag-forced=1
                --edit track:4 --set flag-forced=0
                --edit track:5 --set flag-commentary=1
                --edit track:6 --set flag-commentary=0
                --edit track:7 --set flag-hearing-impaired=1
                --edit track:8 --set flag-hearing-impaired=0
                """.split("\\n");

        MkvFileProcessor mkvFileProcessor = new MkvFileProcessor(new File("mkvtoolnix"), null);
        Method underTest = Arrays.stream(mkvFileProcessor.getClass().getDeclaredMethods()).filter(m -> "getUpdateCommand".equals(m.getName())).findFirst().get();
        underTest.setAccessible(true);
        String[] actualCommand = (String[]) underTest.invoke(mkvFileProcessor, fileInfo);
        String[] trimmedActualCommand = Arrays.copyOfRange(actualCommand, 2, actualCommand.length);
        String actualCommandString = String.join(" ", trimmedActualCommand);
        assertTrue(expectedCommand.length * 4 == trimmedActualCommand.length, "Command length is equal");
        for (String commandPart: expectedCommand) {
            assertTrue(actualCommandString.contains(commandPart));
        }
    }

    private static TrackAttributes t(int id) {
        return new TrackAttributes(id, "", "", false, false, false, false, TrackType.AUDIO);
    }
}
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
import java.nio.charset.StandardCharsets;

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
        assertFalse(sub.hearingImpaired());
        assertFalse(sub.commentary());
        assertEquals(TrackType.SUBTITLES, sub.type());
    }
}
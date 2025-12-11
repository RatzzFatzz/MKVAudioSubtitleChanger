package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackAttributes;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.TrackType;

public class FileInfoTestUtil {
    public static final TrackAttributes AUDIO_GER = new TrackAttributes(0, "ger", "", false, false, false, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_ENG = new TrackAttributes(1, "eng", "", false, false, false, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_GER_DEFAULT = new TrackAttributes(0, "ger", "", true, false, false, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_ENG_DEFAULT = new TrackAttributes(1, "eng", "", true, false, false, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_GER_FORCED = new TrackAttributes(0, "ger", "", false, true, false, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_ENG_FORCED = new TrackAttributes(1, "eng", "", false, true, false, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_GER_COMMENTARY = new TrackAttributes(0, "ger", "", false, false, true, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_ENG_COMMENTARY = new TrackAttributes(1, "eng", "", false, false, true, false, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_GER_HEARING = new TrackAttributes(0, "ger", "", false, false, false, true, TrackType.AUDIO);
    public static final TrackAttributes AUDIO_ENG_HEARING = new TrackAttributes(1, "ger", "", false, false, false, true, TrackType.AUDIO);

    public static final TrackAttributes SUB_GER = new TrackAttributes(0, "ger", "", false, false, false, false, TrackType.SUBTITLES);
    public static final TrackAttributes SUB_ENG = new TrackAttributes(1, "eng", "", false, false, false, false, TrackType.SUBTITLES);
    public static final TrackAttributes SUB_GER_DEFAULT = new TrackAttributes(0, "ger", "", true, false, false, false, TrackType.SUBTITLES);
    public static final TrackAttributes SUB_ENG_DEFAULT = new TrackAttributes(1, "eng", "", true, false, false, false, TrackType.SUBTITLES);
    public static final TrackAttributes SUB_GER_FORCED = new TrackAttributes(0, "ger", "", false, true, false, false, TrackType.SUBTITLES);
    public static final TrackAttributes SUB_ENG_FORCED = new TrackAttributes(1, "eng", "", false, true, false, false, TrackType.SUBTITLES);

    public static TrackAttributes withName(TrackAttributes track, String trackName) {
        return new TrackAttributes(track.id(), track.language(), trackName, track.defaultt(), track.forced(), track.commentary(), track.hearingImpaired(), track.type());
    }
}

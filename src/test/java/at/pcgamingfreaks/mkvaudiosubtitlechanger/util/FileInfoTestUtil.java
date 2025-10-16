package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.LaneType;

public class FileInfoTestUtil {
    public static final FileAttribute AUDIO_GER_DEFAULT = new FileAttribute(0, "ger", "", true, false, LaneType.AUDIO);
    public static final FileAttribute AUDIO_ENG_DEFAULT = new FileAttribute(1, "eng", "", true, false, LaneType.AUDIO);
    public static final FileAttribute AUDIO_GER = new FileAttribute(0, "ger", "", false, false, LaneType.AUDIO);
    public static final FileAttribute AUDIO_ENG = new FileAttribute(1, "eng", "", false, false, LaneType.AUDIO);
    public static final FileAttribute AUDIO_GER_FORCED = new FileAttribute(0, "ger", "", false, true, LaneType.AUDIO);
    public static final FileAttribute AUDIO_ENG_FORCED = new FileAttribute(1, "eng", "", false, true, LaneType.AUDIO);

    public static final FileAttribute SUB_GER_DEFAULT = new FileAttribute(0, "ger", "", true, false, LaneType.SUBTITLES);
    public static final FileAttribute SUB_ENG_DEFAULT = new FileAttribute(1, "eng", "", true, false, LaneType.SUBTITLES);
    public static final FileAttribute SUB_GER = new FileAttribute(0, "ger", "", false, false, LaneType.SUBTITLES);
    public static final FileAttribute SUB_ENG = new FileAttribute(1, "eng", "", false, false, LaneType.SUBTITLES);
    public static final FileAttribute SUB_GER_FORCED = new FileAttribute(0, "ger", "", false, true, LaneType.SUBTITLES);
    public static final FileAttribute SUB_ENG_FORCED = new FileAttribute(1, "eng", "", false, true, LaneType.SUBTITLES);
}

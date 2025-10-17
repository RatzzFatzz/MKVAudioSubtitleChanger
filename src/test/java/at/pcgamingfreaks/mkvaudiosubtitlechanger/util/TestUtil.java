package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;

import java.util.Set;

public class TestUtil {

    public static FileInfo createFileInfoAudio(Set<FileAttribute> defaultAudio, FileAttribute desiredAudio, AttributeConfig config) {
        FileInfo fileInfo = new FileInfo(null);
        fileInfo.setExistingDefaultAudioLanes(defaultAudio);
        fileInfo.setDesiredDefaultAudioLane(desiredAudio);
        fileInfo.setMatchedConfig(config);
        return fileInfo;
    }

    public static FileInfo createFileInfoSubs(Set<FileAttribute> defaultSubtitle, FileAttribute desiredSubtitle, AttributeConfig config) {
        FileInfo fileInfo = new FileInfo(null);
        fileInfo.setExistingDefaultSubtitleLanes(defaultSubtitle);
        fileInfo.setDesiredDefaultSubtitleLane(desiredSubtitle);
        fileInfo.setMatchedConfig(config);
        return fileInfo;
    }

    public static FileInfo createFileInfo(Set<FileAttribute> defaultAudio, FileAttribute desiredAudio,
                                          Set<FileAttribute> defaultSubtitle, FileAttribute desiredSubtitle,
                                          Set<FileAttribute> existingForcedAudioLanes,
                                          Set<FileAttribute> existingForcedSubs, Set<FileAttribute> desiredForcedSubs,
                                          AttributeConfig matchedConfig) {
        FileInfo fileInfo = new FileInfo(null);
        fileInfo.setExistingDefaultAudioLanes(defaultAudio);
        fileInfo.setDesiredDefaultAudioLane(desiredAudio);
        fileInfo.setExistingDefaultSubtitleLanes(defaultSubtitle);
        fileInfo.setDesiredDefaultSubtitleLane(desiredSubtitle);
        fileInfo.setExistingForcedAudioLanes(existingForcedAudioLanes);
        fileInfo.setExistingForcedSubtitleLanes(existingForcedSubs);
        fileInfo.setDesiredForcedSubtitleLanes(desiredForcedSubs);
        fileInfo.setMatchedConfig(matchedConfig);
        return fileInfo;
    }

    public static String[] args(String... args) {
        String[] staticArray = new String[]{"-l", "/", "-a", "jpn:ger"};
        String[] result = new String[staticArray.length + args.length];
        System.arraycopy(staticArray, 0, result, 0, staticArray.length);
        System.arraycopy(args, 0, result, staticArray.length, args.length);
        return result;
    }
}

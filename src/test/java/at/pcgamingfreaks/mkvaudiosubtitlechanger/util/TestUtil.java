package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfo;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.joining;

public class TestUtil {
    public static String yamlList(ConfigProperty main, ConfigProperty... child) {
        return main.prop() + ":\n" + Arrays.stream(child)
                .map(ConfigProperty::prop)
                .collect(joining("\n", "  - ", ""));
    }

    public static <T> Arguments argumentsOf(ConfigProperty property, boolean required, T defaultValue, String yaml, String[] cmd,
                                            ValidationResult result) {
        return Arguments.of(property, required, defaultValue, yaml, cmd, result);
    }

    public static Arguments argumentsOf(ConfigProperty property, boolean required, boolean append, String yaml, String[] cmd,
                                        ValidationResult result, int expectedSize) {
        return Arguments.of(property, required, append, yaml, cmd, result, expectedSize);
    }

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

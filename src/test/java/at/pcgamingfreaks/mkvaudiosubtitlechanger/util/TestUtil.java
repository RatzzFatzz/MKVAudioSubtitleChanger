package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.ValidationResult;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileInfoDto;
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

    public static FileInfoDto createFileInfo(Set<FileAttribute> defaultAudio, FileAttribute desiredAudio) {
        FileInfoDto fileInfoDto = new FileInfoDto(null);
        fileInfoDto.setExistingDefaultAudioLanes(defaultAudio);
        fileInfoDto.setDesiredDefaultAudioLane(desiredAudio);
        return fileInfoDto;
    }

    public static FileInfoDto createFileInfo(Set<FileAttribute> defaultSubtitle, FileAttribute desiredSubtitle, AttributeConfig config) {
        FileInfoDto fileInfoDto = new FileInfoDto(null);
        fileInfoDto.setExistingDefaultSubtitleLanes(defaultSubtitle);
        fileInfoDto.setDesiredDefaultSubtitleLane(desiredSubtitle);
        fileInfoDto.setMatchedConfig(config);
        return fileInfoDto;
    }

    public static String[] args(String... args) {
        String[] staticArray = new String[]{"-l", "/", "-a", "jpn:ger"};
        String[] result = new String[staticArray.length + args.length];
        System.arraycopy(staticArray, 0, result, 0, staticArray.length);
        System.arraycopy(args, 0, result, staticArray.length, args.length);
        return result;
    }
}

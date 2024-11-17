package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;

import java.io.File;
import java.nio.file.Path;

import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix.MKV_MERGER;
import static at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix.MKV_PROP_EDIT;

@Deprecated
public class MkvToolNixPathValidator extends PathValidator {
    private static final String EXE = ".exe";

    public MkvToolNixPathValidator(ConfigProperty property, boolean required, File defaultValue) {
        super(property, required, defaultValue);
    }

    @Override
    protected boolean isValid(File result) {
        return result.isDirectory()
                && (Path.of(result.getAbsolutePath() + "/" + MKV_MERGER + EXE).toFile().isFile()
                && Path.of(result.getAbsolutePath() + "/" + MKV_PROP_EDIT + EXE).toFile().isFile())
                || (Path.of(result.getAbsolutePath() + "/" + MKV_MERGER).toFile().isFile()
                && Path.of(result.getAbsolutePath() + "/" + MKV_PROP_EDIT).toFile().isFile());
    }
}

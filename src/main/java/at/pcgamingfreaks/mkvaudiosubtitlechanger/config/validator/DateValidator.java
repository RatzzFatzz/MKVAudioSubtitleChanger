package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import lombok.extern.slf4j.Slf4j;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

@Slf4j
@Deprecated
public class DateValidator extends ConfigValidator<Date> {
    private static final Date INVALID_DATE = new Date(0);
    private static final Date DEFAULT_DATE = new Date(1000);

    public DateValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    @Override
    protected boolean isOverwritingNecessary() {
        return Config.getInstance().isOnlyNewFiles();
    }

    @Override
    protected Date overwriteValue() {
        try {
            String filePath = AppDirsFactory.getInstance().getUserConfigDir(ProjectUtil.getProjectName(), null, null);
            File lastExecutionFile = Path.of(filePath + "/last-execution.yml").toFile();
            if (!lastExecutionFile.exists()) {
                return DEFAULT_DATE;
            }
            YAML yaml = new YAML(lastExecutionFile);
            return parse(yaml.getString(Config.getInstance().getNormalizedLibraryPath(), DateUtils.convert(DEFAULT_DATE)));
        } catch (YamlInvalidContentException | IOException e) {
            log.error("Couldn't open last-execution.properties", e);
            return INVALID_DATE;
        }
    }

    @Override
    Date parse(String value) {
        return DateUtils.convert(value, INVALID_DATE);
    }

    @Override
    boolean isValid(Date result) {
        return !result.equals(INVALID_DATE);
    }

    @Override
    public int getWeight() {
        return 40;
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.config.validator;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class DateValidator extends ConfigValidator<Date> {
    private static final Date DEFAULT_DATE = new Date(0);

    public DateValidator(ConfigProperty property, boolean required) {
        super(property, required, null);
    }

    @Override
    protected boolean isOverwritingNecessary() {
        return Config.getInstance().isOnlyNewFiles();
    }

    @Override
    protected Date overwriteValue() {
        Properties prop = new Properties();
        try {
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/last-execution.properties"));
            return parse(prop.getProperty(Config.getInstance().getLibraryPath().getAbsolutePath()));
        } catch (IOException e) {
            log.error("Couldn't open last-execution.properties");
            return DEFAULT_DATE;
        }
    }

    @Override
    Date parse(String value) {
        return DateUtils.convert(value, DEFAULT_DATE);
    }

    @Override
    boolean isValid(Date result) {
        return !result.equals(DEFAULT_DATE);
    }

    protected static Set<Integer> getDependentValidators() {
        return Set.of(PathValidator.getWeight(), BooleanValidator.getWeight());
    }
}

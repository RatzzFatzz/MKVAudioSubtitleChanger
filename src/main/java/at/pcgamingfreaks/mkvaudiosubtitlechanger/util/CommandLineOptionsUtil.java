package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ConfigProperty;
import org.apache.commons.cli.Option;

public class CommandLineOptionsUtil {
    public static Option optionOf(ConfigProperty property, String opt, int args) {
        return optionOf(property, opt, args, false);
    }

    public static Option optionOf(ConfigProperty property, String opt, boolean hasArg, boolean required) {
        return optionOf(property, opt, hasArg ? 1 : 0, required);
    }

    public static Option optionOf(ConfigProperty property, String opt, int args, boolean required) {
        Option option = new Option(opt, property.desc());
        option.setArgs(args);
        option.setLongOpt(property.prop());
        option.setRequired(required);
        return option;
    }

}

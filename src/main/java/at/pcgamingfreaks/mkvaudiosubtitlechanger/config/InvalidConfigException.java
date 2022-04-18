package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

public class InvalidConfigException extends RuntimeException{
    public InvalidConfigException(ConfigErrors errors) {
        super("Errors in config: " + errors);
    }
}

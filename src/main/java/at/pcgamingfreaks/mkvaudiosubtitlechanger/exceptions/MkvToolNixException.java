package at.pcgamingfreaks.mkvaudiosubtitlechanger.exceptions;

public class MkvToolNixException extends RuntimeException{

    public MkvToolNixException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage().replaceAll("\\r|\\n", " ");
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import org.apache.logging.log4j.Logger;

public class LogUtils {

    public static <T>  void ifDebug(Logger log, T object) {
        if (log.isDebugEnabled()) {
            log.debug(object);
        }
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

    public static Date convert(long millis) {
        return new Date(millis);
    }

    /**
     * Convert String to date.
     * @return parsed date, null if exception occurs
     */
    public static Date convert(String date, Date defaultDate) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return defaultDate;
        }
    }

    public static String convert(Date date) {
        return dateFormat.format(date);
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void convert() {
        Date expectedDate = new Date(0);
        String expectedString = "01.01.1970-01:00:00";

        assertEquals(expectedDate, DateUtils.convert(0));
        assertEquals(expectedDate, DateUtils.convert(expectedString, expectedDate));
        assertEquals(expectedDate, DateUtils.convert("1234;15", expectedDate));
        assertEquals(expectedString, DateUtils.convert(expectedDate));
    }
}
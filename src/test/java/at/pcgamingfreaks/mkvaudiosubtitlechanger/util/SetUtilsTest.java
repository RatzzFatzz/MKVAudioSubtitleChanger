package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetUtilsTest {

    @Test
    void retainOf() {
        List<Integer> list1 = List.of(1, 2, 3, 4, 5);
        List<Integer> list2 = List.of(2, 4, 6, 8, 10);
        Set<Integer> expected = Set.of(2, 4);

        assertEquals(expected, SetUtils.retainOf(list1, list2));
    }
}
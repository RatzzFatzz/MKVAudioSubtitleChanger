package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUtils {
    public static <T> Set<T> retainOf(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<>(list1);
        set.retainAll(list2);
        return set;
    }
}

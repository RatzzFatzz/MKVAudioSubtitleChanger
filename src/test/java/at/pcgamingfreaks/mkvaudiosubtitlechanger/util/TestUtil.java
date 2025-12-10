package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

public class TestUtil {

    public static String[] args(String... args) {
        String[] staticArray = new String[]{"-l", "/", "-a", "jpn:ger"};
        String[] result = new String[staticArray.length + args.length];
        System.arraycopy(staticArray, 0, result, 0, staticArray.length);
        System.arraycopy(args, 0, result, staticArray.length, args.length);
        return result;
    }
}

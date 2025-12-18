package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

public class TestUtil {

    public static String[] args(String... args) {
        String[] staticArray = new String[]{"-a", "jpn:ger", "/"};
        String[] result = new String[staticArray.length + args.length];
        System.arraycopy(args, 0, result, 0, args.length);
        System.arraycopy(staticArray, 0, result, args.length, staticArray.length);
        return result;
    }
}

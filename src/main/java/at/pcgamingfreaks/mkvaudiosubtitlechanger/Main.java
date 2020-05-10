package at.pcgamingfreaks.mkvaudiosubtitlechanger;

public class Main {
    public static void main(String[] args) {
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel();
        kernel.execute();
    }
}

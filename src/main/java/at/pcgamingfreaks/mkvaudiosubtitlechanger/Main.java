package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import config.MKVToolProperties;

public class Main {
    public static void main(String[] args) {
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel();
        kernel.execute();
    }
}

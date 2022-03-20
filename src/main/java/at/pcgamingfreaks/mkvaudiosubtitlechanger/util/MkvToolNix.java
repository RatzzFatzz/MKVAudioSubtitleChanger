package at.pcgamingfreaks.mkvaudiosubtitlechanger.util;

public enum MkvToolNix {
    MKV_MERGER("mkvmerge.exe"),
    MKV_PROP_EDIT("mkvpropedit.exe");

    private final String file;

    MkvToolNix(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return file;
    }
}

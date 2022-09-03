package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MkvToolNix {
    MKV_MERGER("mkvmerge"),
    MKV_PROP_EDIT("mkvpropedit");

    private final String file;

    @Override
    public String toString() {
        return file;
    }
}

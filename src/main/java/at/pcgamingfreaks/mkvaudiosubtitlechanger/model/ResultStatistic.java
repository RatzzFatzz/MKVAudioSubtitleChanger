package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;

@Getter
public class ResultStatistic {
    private static final String result = "Files should change: %s%n" +
            "Files successfully changed: %s%n" +
            "Files failed changing: %s%n" +
            "Files already fitting config: %s%n" +
            "Runtime: %ss";

    private int filesShouldChange = 0;
    private int filesSuccessfullyChanged = 0;
    private int filesFailed = 0;
    private int filesAlreadyFit = 0;
    @Getter(AccessLevel.NONE)
    private long startTime = 0;
    private long runtime = 0;

    public void shouldChange(File file, FileInfoDto fileInfo) {
        filesShouldChange++;
    }

    public void success(File file, FileInfoDto fileInfo) {
        filesSuccessfullyChanged++;
    }

    public void failure(File file, FileInfoDto fileInfo) {
        filesFailed++;
    }

    public void fits(File file, FileInfoDto fileInfo) {
        filesAlreadyFit++;
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        runtime = System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return String.format(result, filesShouldChange, filesSuccessfullyChanged, filesFailed, filesAlreadyFit,
                runtime / 1000);
    }
}

package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;

@Getter
public class ResultStatistic {
    private static final String result = "Total files: %s%n" +
            "├─ Should change: %s%n" +
            "├─ Successfully changed: %s%n" +
            "├─ Already fit config: %s%n" +
            "└─ Failed: %s%n" +
            "Runtime: %ss";

    private int filesTotal = 0;
    private int filesShouldChange = 0;
    private int filesSuccessfullyChanged = 0;
    private int filesFailed = 0;
    private int filesAlreadyFit = 0;
    @Getter(AccessLevel.NONE)
    private long startTime = 0;
    private long runtime = 0;

    public synchronized void total() {
        filesTotal++;
    }

    public synchronized void shouldChange() {
        filesShouldChange++;
    }

    public synchronized void success() {
        filesSuccessfullyChanged++;
    }

    public synchronized void failure() {
        filesFailed++;
    }

    public synchronized void fits() {
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
        return String.format(result, filesTotal, filesShouldChange, filesSuccessfullyChanged, filesAlreadyFit,
                filesFailed, runtime / 1000);
    }
}

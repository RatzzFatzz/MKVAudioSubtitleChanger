package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class ResultStatistic {
    private static final String result = "Total files: %s%n" +
            "├─ Should change: %s%n" +
            "│  ├─ Failed changing: %s%n" +
            "│  └─ Successfully changed: %s%n" +
            "├─ No suitable config found: %s%n" +
            "├─ Already fit config: %s%n" +
            "└─ Failed: %s%n" +
            "Runtime: %ss";

    private int filesTotal = 0;

    private int shouldChange = 0;
    private int failedChanging = 0;
    private int successfullyChanged = 0;

    private int noSuitableConfigFound = 0;
    private int alreadyFits = 0;
    private int failed = 0;

    @Getter(AccessLevel.NONE)
    private long startTime = 0;
    private long runtime = 0;

    public synchronized void total() {
        filesTotal++;
    }

    public synchronized void shouldChange() {
        shouldChange++;
    }

    public synchronized void success() {
        successfullyChanged++;
    }

    public synchronized void failedChanging() {
        failedChanging++;
    }

    public synchronized void noSuitableConfigFound() {
        noSuitableConfigFound++;
    }

    public synchronized void alreadyFits() {
        alreadyFits++;
    }

    public synchronized void failure() {
        failed++;
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        runtime = System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return String.format(result, filesTotal, shouldChange, failedChanging, successfullyChanged,
                noSuitableConfigFound, alreadyFits, failed, runtime / 1000);
    }
}

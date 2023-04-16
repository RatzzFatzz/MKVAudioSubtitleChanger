package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ResultStatistic {
    private static final String result = "Total files: %s%n" +
            "├─ Excluded: %s%n" +
            "├─ Should change: %s%n" +
            "│  ├─ Failed changing: %s%n" +
            "│  └─ Successfully changed: %s%n" +
            "├─ No suitable config found: %s%n" +
            "├─ Already fit config: %s%n" +
            "└─ Failed: %s%n" +
            "Runtime: %s";
    private static ResultStatistic instance;
    private int filesTotal = 0;
    private int excluded = 0;

    private int shouldChange = 0;
    private int failedChanging = 0;
    private int successfullyChanged = 0;

    private int noSuitableConfigFound = 0;
    private int alreadyFits = 0;
    private int failed = 0;

    @Getter(AccessLevel.NONE)
    private long startTime = 0;
    private long runtime = 0;

    public static ResultStatistic getInstance() {
        if (instance == null) {
            instance = new ResultStatistic();
        }
        return instance;
    }

    public void increaseTotalBy(int amount) {
        filesTotal += amount;
    }

    public synchronized void total() {
        filesTotal++;
    }

    public void increaseExcludedBy(int amount) {
        excluded += amount;
    }

    public synchronized void excluded() {
        excluded++;
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

    public void printResult() {
        System.out.println(prettyPrint());
        log.info(this.toString());
    }

    private String formatTimer() {
        int seconds = (int) (runtime / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        if (days >= 1) {
            return String.format("%sd %sh %sm %ss", days, hours % 24, minutes % 60, seconds % 60);
        } else if (hours >= 1) {
            return String.format("%sh %sm %ss", hours, minutes % 60, seconds % 60);
        } else if (minutes >= 1) {
            return String.format("%sm %ss", minutes, seconds % 60);
        } else {
            return String.format("%ss", seconds % 60);
        }
    }

    public String prettyPrint() {
        return String.format(result, filesTotal, excluded, shouldChange, failedChanging, successfullyChanged,
                noSuitableConfigFound, alreadyFits, failed, formatTimer());
    }

    @Override
    public String toString() {
        return "ResultStatistic: " + "filesTotal=" + filesTotal +
                ", excluded=" + excluded +
                ", shouldChange=" + shouldChange +
                " (failedChanging=" + failedChanging +
                ", successfullyChanged=" + successfullyChanged +
                "), noSuitableConfigFound=" + noSuitableConfigFound +
                ", alreadyFits=" + alreadyFits +
                ", failed=" + failed +
                ", runtime=" + formatTimer();
    }
}

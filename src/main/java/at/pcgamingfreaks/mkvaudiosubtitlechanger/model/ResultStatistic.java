package at.pcgamingfreaks.mkvaudiosubtitlechanger.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ResultStatistic {
    private static final String PRINT_TEMPLATE = "Total: %s, Changing: %s (Successful: %s, Failed %s), Unchanged: %s, Excluded: %s, Unknown/Failed: %s\nRuntime: %s";
    private static ResultStatistic instance;

    private int changePlanned = 0;
    private int changeFailed = 0;
    private int changeSuccessful = 0;
    private int unchanged = 0;
    private int excluded = 0;
    private int unknownFailed = 0;

    private long startTime = 0;
    private long runtime = 0;

    public static ResultStatistic getInstance() {
        return getInstance(false);
    }

    public static ResultStatistic getInstance(boolean reset) {
        if (instance == null || reset) {
            instance = new ResultStatistic();
        }
        return instance;
    }

    public int total() {
        return changePlanned + unchanged + excluded + unknownFailed;
    }

    public synchronized void changePlanned() {
        changePlanned++;
    }

    public synchronized void changeSuccessful() {
        changeSuccessful++;
    }

    public synchronized void changeFailed() {
        changeFailed++;
    }

    public synchronized void unchanged() {
        unchanged++;
    }

    public synchronized void increaseUnchangedBy(int amount) {
        unchanged += amount;
    }

    public synchronized void excluded() {
        excluded++;
    }

    public synchronized void unknownFailed() {
        unknownFailed++;
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        runtime = System.currentTimeMillis() - startTime;
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

    public void print() {
        String result = this.toString();
        System.out.println(result);
        log.info(result);
    }

    @Override
    public String toString() {
        return String.format(PRINT_TEMPLATE, total(), changePlanned, changeSuccessful, changeFailed, unchanged, excluded, unknownFailed, formatTimer());
    }
}

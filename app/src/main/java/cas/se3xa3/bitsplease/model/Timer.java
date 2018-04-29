package cas.se3xa3.bitsplease.model;

/**
 * Created on 11/11/2015.
 */
public class Timer {
    private long start;
    private long stop;

    public Timer() {
        start = System.currentTimeMillis();
        stop = start;
    }

    /**
     * Restart the timer setting the start time to the current time.
     */
    public void restart() {
        start = System.currentTimeMillis();
        stop = start;
    }

    /**
     * Mark the current time as the time the timer stopped.<br>
     * Calling this method will overwrite the last marked stop.
     */
    public void markStop() {
        stop = System.currentTimeMillis();
    }

    /**
     * Calculate the total time from start to the last marked stop.
     * @return the time between the last marked stop and the start.
     */
    public long getTotalTime() {
        return stop - start;
    }
}

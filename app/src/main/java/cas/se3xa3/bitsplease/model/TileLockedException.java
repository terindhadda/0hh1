package cas.se3xa3.bitsplease.model;

/**
 * Created on 31/10/2015.
 * Thrown when a locked tile is updated.
 */
public class TileLockedException extends RuntimeException {
    public TileLockedException(String s) {
        super(s);
    }

    public TileLockedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TileLockedException(Throwable throwable) {
        super(throwable);
    }
}

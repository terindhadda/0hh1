package cas.se3xa3.bitsplease.controller;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.checker.BoardChecker;
import cas.se3xa3.bitsplease.model.checker.Result;
import cas.se3xa3.bitsplease.view.BoardView;
import cas.se3xa3.bitsplease.view.GameBoard;

import javax.swing.*;

/**
 * A board checker that updates the given view accordingly. It's
 * main function is to allow for a delayed execution. This function
 * is useful in giving the user a buffer of {@link #DELAY}ms before
 * to finish their tile update before running the check.
 * Created on 11/11/2015.
 */
public class Validator extends Thread {
    public static final long DELAY = 500L;
    public static final long PRECISION = 100L;

    private static final long NO_SCHEDULE = -1L;

    private final Board board;
    private GameBoard view;
    private final BoardChecker checker;
    private Runnable callback;
    private volatile long scheduledExecTime = 0;
    private boolean stop = false;
    private boolean isSolved = false;

    /**
     * Create a new validator for the given board and it's
     * corresponding view.
     * @param board the board being played.
     * @param view the view for {@code board}.
     * @param runOnSolved a callback to be run when the board is solved.
     */
    public Validator(Board board, GameBoard view, Runnable runOnSolved) {
        super("Board validator");
        this.board = board;
        this.view = view;
        this.callback = runOnSolved;
        this.checker = new BoardChecker(board);
        this.scheduledExecTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        Runnable check = () -> {
            synchronized (board) {
                if (!board.isFull()) {
                    Result result;
                    synchronized (checker) {
                        result = checker.isValid();
                    }
                    if (result.getResultState() == Result.State.VIOLATES) {
                        view.boardView().setHighlights(result.getErrorCoords());
                        view.setMessage(result.getMessage());
                    }
                } else {
                    Result result;
                    synchronized (checker) {
                        result = checker.isSolved();
                    }
                    if (result.getResultState() == Result.State.VIOLATES) {
                        //Nope, guess not
                        view.boardView().setHighlights(result.getErrorCoords());
                        view.setMessage(result.getMessage());
                    } else {
                        //Run the callback and complete the service
                        callback.run();
                        isSolved = true;
                    }
                }
            }
        };
        while (!stop && !isSolved) {
            if (scheduledExecTime != NO_SCHEDULE
                    && System.currentTimeMillis() > scheduledExecTime) {
                //Run the check on the awt thread
                SwingUtilities.invokeLater(check);
                scheduledExecTime = NO_SCHEDULE;
            }
            try {
                Thread.sleep(PRECISION);
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Peacefully shutdown this validator. Once shutdown
     * the instance cannot be restarted.
     */
    public void shutdown() {
        this.stop = true;
    }

    /**
     * Set the execution date to {@link #DELAY}ms in the future.<br>
     * This will cancel the current schedule if one exists.
     */
    public synchronized void schedule() {
        this.scheduledExecTime = System.currentTimeMillis() + DELAY;
    }

    /**
     * Check if this validator has marked this board as solved.
     * @return true if the board is solved, false if the board is
     * not solved or this validator has been {@link #shutdown()}
     * before the board was solved.
     */
    public synchronized boolean isSolved() {
        return this.isSolved;
    }

    /**
     * Direct access to this {@link Validator}'s {@link BoardChecker}.
     * @return the result of the {@link BoardChecker#isValid()} call.
     */
    public synchronized Result isValid() {
        synchronized (checker) {
            return checker.isValid();
        }
    }
}

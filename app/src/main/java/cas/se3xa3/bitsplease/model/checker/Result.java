package cas.se3xa3.bitsplease.model.checker;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Coordinate;

import java.util.Set;

/**
 * Created on 25/10/2015.
 * A data object containing information about the rule enforcement. <br>
 * See {@link Rule#holds(Board)} and {@link Rules} for some implementations
 */
public class Result {
	//Checks for any violations in the board, return a message stating what was wrong, and returns the coordinates which were invalid
    public enum State {SATISFIES, VIOLATES}

    private State resultState;
    private String message;
    private Set<Coordinate> errorCoords;

    public Result(State resultState, String message, Set<Coordinate> errorCoords) {
        this.resultState = resultState;
        this.message = message;
        this.errorCoords = errorCoords;
    }

    /**
     * Get the state of this result. One of  or
     * @return {@link State#SATISFIES} if the rule holds and {@link State#VIOLATES} if the rule doesn't
     */
    public State getResultState() {
        return resultState;
    }

    public void setResultState(State resultState) {
        this.resultState = resultState;
    }

    /**
     * A short message describing this result.
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The invalid {@link Coordinate}s violating the rule.
     * @return a set of {@link Coordinate} with the violating tile locations
     * or null if {@link Result#getResultState()} == {@link State#SATISFIES}.
     */
    public Set<Coordinate> getErrorCoords() {
        return errorCoords;
    }

    public void setErrorCoords(Set<Coordinate> errorCoords) {
        this.errorCoords = errorCoords;
    }
}

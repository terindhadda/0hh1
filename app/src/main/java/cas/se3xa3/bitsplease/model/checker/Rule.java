package cas.se3xa3.bitsplease.model.checker;

import cas.se3xa3.bitsplease.model.Board;

/**
 * Created on 25/10/2015.
 * These rules are designed to return information about how a rule
 * is being violated if it is.
 */
@FunctionalInterface
public interface Rule {
    /**
     * Checks if this rule is satisfied by the {@code board}.
     * @param board the board being checked.
     * @return a result stating that the rule holds, or why it doesn't hold
     */
    Result holds(Board board);
}

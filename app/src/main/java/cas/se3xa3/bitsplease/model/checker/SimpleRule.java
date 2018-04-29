package cas.se3xa3.bitsplease.model.checker;

import cas.se3xa3.bitsplease.model.Board;

/**
 * Created on 02/11/2015.
 * This is a class of rules that are optimized as the result
 * is not concerned with additional metadata.
 */
@FunctionalInterface
public interface SimpleRule {

    /**
     * Checks if this rule is satisfied by the {@code board}.
     * @param board the board being checked.
     * @return true if the rule holds, false otherwise
     */
    boolean holds(Board board);
}

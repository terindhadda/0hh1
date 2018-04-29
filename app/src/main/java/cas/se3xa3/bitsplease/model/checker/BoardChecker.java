package cas.se3xa3.bitsplease.model.checker;


import cas.se3xa3.bitsplease.model.Board;

import java.util.*;

/**
 * Created on 25/10/2015.
 * A board checker that checks the validity of the board it wraps.
 */
public class BoardChecker {
    private final Random r = new Random(System.currentTimeMillis());

    private List<Rule> rules;
    private Board board;

    public BoardChecker(Board board) {
        this.board = board;
        rules = Arrays.asList(
                Rules.MAX_2_CONSECUTIVE_ROW, Rules.MAX_2_CONSECUTIVE_COLUMN,
                Rules.EQUAL_TILE_COUNT_ROW, Rules.EQUAL_TILE_COUNT_COLUMN,
                Rules.NO_IDENTICAL_ROWS, Rules.NO_IDENTICAL_COLUMNS
        );
    }

    /**
     * Check if the board is valid, not necessarily solved. See {@link BoardChecker#isSolved()}
     * for that check.
     * @return a {@link Result} containing one of the errors on the board or a successful result.
     * Check {@link Result#getResultState()} for this evaluation.
     */
    public Result isValid() {
        List<Result> badResults = new ArrayList<>();
        //Check all the rules
        rules.forEach(rule -> {
            Result result = rule.holds(board);
            if (result.getResultState() == Result.State.VIOLATES) badResults.add(result);
        });
        //Return success or a random violation from the list of violations
        if (badResults.isEmpty()) return new Result(Result.State.SATISFIES, "Valid", null);
        else                      return badResults.get(r.nextInt(badResults.size()));
    }

    /**
     * Check if the board is solved, meaning full and valid. See {@link BoardChecker#isValid()}
     * for a validity check on a no-full board.
     * @return a {@link Result} containing one of the errors on the board or a successful result.
     * Check {@link Result#getResultState()} for this evaluation.
     */
    public Result isSolved() {
        Result full = Rules.NO_EMPTY_TILES.holds(board);
        if (full.getResultState() != Result.State.SATISFIES) return full;
        Result valid = isValid();
        if (valid.getResultState() == Result.State.SATISFIES)
            return new Result(Result.State.SATISFIES, "Solved", null);
        else
            return valid;
    }
}

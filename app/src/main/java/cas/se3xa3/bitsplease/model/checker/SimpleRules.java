package cas.se3xa3.bitsplease.model.checker;

import java.util.stream.IntStream;

import static cas.se3xa3.bitsplease.model.Tile.*;

/**
 * Created on 02/11/2015.
 * A set of {@link SimpleRule} implementations.
 * These rule implementations are designed to short circuit as quick as possible.
 */
public class SimpleRules {
    /**
     * Verifies that no rows or columns contain 3 red or 3 blue in a row
     */
    public static final SimpleRule NO_THREE_IN_A_ROW = board ->
            //Check all the rows
            IntStream.range(0, board.getSize()).allMatch(index -> {
                int consBlueRow = 0;
                int consRedRow = 0;
                int consBlueCol = 0;
                int consRedCol = 0;
                for (int perpIndex = 0; perpIndex < board.getSize(); perpIndex++) {
                    switch (board.getTileAt(index, perpIndex)) {
                        case EMPTY:
                            consRedRow = 0;
                            consBlueRow = 0;
                            break;
                        case RED:
                            consRedRow++;
                            consBlueRow = 0;
                            break;
                        case BLUE:
                            consRedRow = 0;
                            consBlueRow++;
                            break;
                    }
                    switch (board.getTileAt(perpIndex, index)) {
                        case EMPTY:
                            consRedCol = 0;
                            consBlueCol = 0;
                            break;
                        case RED:
                            consRedCol++;
                            consBlueCol = 0;
                            break;
                        case BLUE:
                            consRedCol = 0;
                            consBlueCol++;
                            break;
                    }
                    if (consBlueCol == 3 || consBlueRow == 3 || consRedCol == 3 || consRedRow == 3) return false;
                }
                return true;
            });

    /**
     * Verify that the number of red and blue in each row or column does not
     * exceed the number of tiles in half of the board.
     */
    public static final SimpleRule EQUAL_BLUE_AND_RED = board ->
        IntStream.range(0, board.getSize()).allMatch(index ->
                board.countTypeInRow(index, RED) <= board.getSize() / 2
                && board.countTypeInRow(index, BLUE) <= board.getSize() / 2
                && board.countTypeInColumn(index, RED) <= board.getSize() / 2
                && board.countTypeInColumn(index, BLUE) <= board.getSize() / 2
        );

    /**
     * Verify that no identical rows or columns are the same.
     */
    public static final SimpleRule NO_IDENTICAL_ROWS_OR_COLUMNS = board -> IntStream.range(0, board.getSize()).map(row -> IntStream.range(0, board.getSize()).reduce(0, (serRow, col) -> {
        if (board.getTileAt(row, col) == RED) return serRow | (1 << col);
        else return serRow;
    })).distinct().count() == board.getSize() && IntStream.range(0, board.getSize()).map(col -> IntStream.range(0, board.getSize()).reduce(0, (serCol, row) -> {
        if (board.getTileAt(row, col) == RED) return serCol | (1 << col);
        else return serCol;
    })).distinct().count() == board.getSize();
}

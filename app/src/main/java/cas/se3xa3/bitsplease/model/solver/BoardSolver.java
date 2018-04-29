package cas.se3xa3.bitsplease.model.solver;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Coordinate;
import cas.se3xa3.bitsplease.model.Tile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static cas.se3xa3.bitsplease.model.Tile.*;

/**
 * Created on 15/09/2015.
 * A board solver that can be asked to solve the board it wraps. See
 * {@link BoardSolver#placeCorrectTile()} for single tile placement or
 * {@link BoardSolver#tryToSolve()} for a best attempt at a solution. All
 * solvable boards will cause {@link BoardSolver#tryToSolve()} to return true.
 */
public class BoardSolver {
    private Board board;

    /**
     * Create a new solver for {@code board}.
     * @param board the board the solver is operating on.
     */
    public BoardSolver(Board board) {
        this.board = board;
    }

    public Hint requestHint() {
        if (board.isFull()) return new Hint(null, "Board is full.");
        Set<Coordinate> coords = new HashSet<Coordinate>();
        Coordinate coord = findDuos(false);
        if (coord != null) {
            coords.add(coord);
            return new Hint(coords,
                    "Three of the same color tiles are not allowed next to each other.");
        }
        coord = findTrios(false);
        if (coord != null) {
            coords.add(coord);
            return new Hint(coords,
                    "Three of the same color tiles are not allowed next to each other.");
        }
        coord = fillInRowCount(false);
        if (coord != null) {
            int row = coord.getRow();
            IntStream.range(0, board.getSize()).forEach(col -> coords.add(new Coordinate(row, col)));
            return new Hint(coords,
                    "Rows have an equal number of each color.");
        }
        coord = fillInColumnCount(false);
        if (coord != null) {
            int col = coord.getColumn();
            IntStream.range(0, board.getSize()).forEach(row -> coords.add(new Coordinate(row, col)));
            return new Hint(coords,
                    "Columns have an equal number of each color.");
        }
        Coordinate[] coordAndMatching = findUniqueRow(false);
        if (coordAndMatching != null) {
            int row = coordAndMatching[0].getRow();
            int matchingRow = coordAndMatching[1].getRow();
            IntStream.range(0, board.getSize()).forEach(col -> {
                coords.add(new Coordinate(row, col));
                coords.add(new Coordinate(matchingRow, col));
            });
            return new Hint(coords,
                    "No two rows are the same.");
        }
        coordAndMatching = findUniqueColumn(false);
        if (coordAndMatching != null) {
            int col = coordAndMatching[0].getColumn();
            int matchingCol = coordAndMatching[1].getColumn();
            IntStream.range(0, board.getSize()).forEach(row -> {
                coords.add(new Coordinate(row, col));
                coords.add(new Coordinate(row, matchingCol));
            });
            return new Hint(coords,
                    "No two columns are the same.");
        }
        return new Hint(null, "I don't know what to do :(");
    }

    /**
     * Attempts to place a correct tile on the board. This method evaluates
     * the board and acts accordingly. If the board is not valid this method
     * may still place a tile that satisfies some constraints. <br>
     * See {@link cas.se3xa3.bitsplease.model.checker.BoardChecker} to verify
     * board validity.
     * @return the {@link Coordinate} of the placed tile or null if there is
     * no location that the solver is confident enough to place.
     */
    public Coordinate placeCorrectTile() {
        Coordinate placedTileAt;
        if ((placedTileAt = findDuos(true)) != null) {
            return placedTileAt;
        }
        if ((placedTileAt = findTrios(true)) != null) {
            return placedTileAt;
        }
        if ((placedTileAt = fillInRowCount(true)) != null) {
            return placedTileAt;
        }
        if ((placedTileAt = fillInColumnCount(true)) != null) {
            return placedTileAt;
        }
        Coordinate[] placedTileAtArr;
        if ((placedTileAtArr = findUniqueRow(true)) != null) {
            return placedTileAtArr[0];
        }
        if ((placedTileAtArr = findUniqueColumn(true)) != null) {
            return placedTileAtArr[0];
        }
        return null;
    }

    public boolean tryToSolve() {
        while (!board.isFull()) {
            if (placeCorrectTile() == null) return false;
        }
        return true;
    }

    private Coordinate findDuos(boolean place) {
        Tile tileCache1;
        Tile tileCache2;
        //Check rows.
        for (int row = 0; row <= board.getMaxIndex(); row++) {
            for (int col = 0; col <= board.getMaxIndex(); col++) {
                if (board.getTileAt(row, col) == EMPTY) {
                    //We have an empty space. Check it for duos
                    //Check 2 to the left
                    if (col >= 2) {
                        //There is space for 2 tiles to the left of current
                        tileCache1 = board.getTileAt(row, col - 1);
                        tileCache2 = board.getTileAt(row, col - 2);
                        if (tileCache1 != EMPTY && tileCache1 == tileCache2) {
                            //We have a duo!
                            if (place) board.setTileAt(row, col, tileCache1.opposite());
                            return new Coordinate(row, col);
                        }
                    }
                    //Check 2 to the left
                    if (col <= board.getMaxIndex() - 2) {
                        //We have space for 2 tiles to the right of current
                        tileCache1 = board.getTileAt(row, col + 1);
                        tileCache2 = board.getTileAt(row, col + 2);
                        if (tileCache1 != EMPTY && tileCache1 == tileCache2) {
                            //We have a duo!
                            if (place) board.setTileAt(row, col, tileCache1.opposite());
                            return new Coordinate(row, col);
                        }
                    }
                    //check 2 to the top
                    if (row >= 2) {
                        //There is space for 2 tiles to the top of current
                        tileCache1 = board.getTileAt(row - 1, col);
                        tileCache2 = board.getTileAt(row - 2, col);
                        if (tileCache1 != EMPTY && tileCache1 == tileCache2) {
                            //We have a duo!
                            if (place) board.setTileAt(row, col, tileCache1.opposite());
                            return new Coordinate(row, col);
                        }
                    }
                    //check 2 to the bottom
                    if (row <= board.getMaxIndex() - 2) {
                        //We have space for 2 tiles to the bottom of current
                        tileCache1 = board.getTileAt(row + 1, col);
                        tileCache2 = board.getTileAt(row + 2, col);
                        if (tileCache1 != EMPTY && tileCache1 == tileCache2) {
                            //We have a duo!
                            if (place) board.setTileAt(row, col, tileCache1.opposite());
                            return new Coordinate(row, col);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Coordinate findTrios(boolean place) {
        Tile tileCache1;
        Tile tileCache2;
        //Check rows.
        for (int row = 0; row <= board.getMaxIndex(); row++) {
            for (int col = 0; col <= board.getMaxIndex(); col++) {
                if (board.getTileAt(row, col) == EMPTY) {
                    //We have an empty space. Check it for trio
                    //Check sitting between 2 horizontal
                    if (col >= 1 && col <= board.getMaxIndex() - 1) {
                        //We have an empty tile with at least 1 space on either side
                        tileCache1 = board.getTileAt(row, col - 1);
                        tileCache2 = board.getTileAt(row, col + 1);
                        if (tileCache1 != EMPTY && tileCache1 == tileCache2) {
                            //We have a duo!
                            if (place) board.setTileAt(row, col, tileCache1.opposite());
                            return new Coordinate(row, col);
                        }
                    }
                    //Check sitting between 2 vertical
                    if (row >= 1 && row <= board.getMaxIndex() - 1) {
                        //We have an empty tile with at least 1 space on either side
                        tileCache1 = board.getTileAt(row - 1, col);
                        tileCache2 = board.getTileAt(row + 1, col);
                        if (tileCache1 != EMPTY && tileCache1 == tileCache2) {
                            //We have a duo!
                            if (place) board.setTileAt(row, col, tileCache1.opposite());
                            return new Coordinate(row, col);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Coordinate fillInRowCount(boolean place) {
        Tile current;
        int numBlue = 0;
        int numRed = 0;
        //Check rows.
        for (int row = 0; row <= board.getMaxIndex(); row++) {
            for (int col = 0; col <= board.getMaxIndex(); col++) {
                current = board.getTileAt(row, col);
                if (current == EMPTY) continue;
                if (current == BLUE)  numBlue++;
                else                  numRed++;
            }
            //if there are an equivalent amount we can't do anything.
            //this also catches the case where the row is full
            if (numBlue != numRed) {
                if (numBlue == this.board.getSize() / 2) {
                    //Half the row is filled with BLUE, the rest must be RED;
                    for (int col = 0; col <= board.getMaxIndex(); col++) {
                        if (board.getTileAt(row, col) == EMPTY) {
                            if (place) board.setTileAt(row, col, RED);
                            return new Coordinate(row, col);
                        }
                    }
                } else if (numRed == this.board.getSize() / 2) {
                    //Half the row is filled with RED, the rest must be BLUE;
                    for (int col = 0; col <= board.getMaxIndex(); col++) {
                        if (board.getTileAt(row, col) == EMPTY) {
                            if (place) board.setTileAt(row, col, BLUE);
                            return new Coordinate(row, col);
                        }
                    }
                }
            }
            //Reset for next iteration
            numBlue = 0;
            numRed = 0;
        }
        return null;
    }

    private Coordinate fillInColumnCount(boolean place) {
        Tile current;
        int numBlue = 0;
        int numRed = 0;
        //Check cols.
        for (int col = 0; col <= board.getMaxIndex(); col++) {
            for (int row = 0; row <= board.getMaxIndex(); row++) {
                current = board.getTileAt(row, col);
                if (current == EMPTY) continue;
                if (current == BLUE)  numBlue++;
                else                  numRed++;
            }
            //if there are an equivalent amount we can't do anything.
            //this also catches the case where the row is full
            if (numBlue != numRed) {
                if (numBlue == this.board.getSize() / 2) {
                    //Half the row is filled with BLUE, the rest must be RED;
                    for (int row = 0; row <= board.getMaxIndex(); row++) {
                        if (board.getTileAt(row, col) == EMPTY) {
                            if (place) board.setTileAt(row, col, RED);
                            return new Coordinate(row, col);
                        }
                    }
                } else if (numRed == this.board.getSize() / 2) {
                    //Half the row is filled with RED, the rest must be BLUE;
                    for (int row = 0; row <= board.getMaxIndex(); row++) {
                        if (board.getTileAt(row, col) == EMPTY) {
                            if (place) board.setTileAt(row, col, BLUE);
                            return new Coordinate(row, col);
                        }
                    }
                }
            }
            //Reset for next iteration
            numBlue = 0;
            numRed = 0;
        }
        return null;
    }

    private Coordinate[] findUniqueRow(boolean place) {
        for (int row = 0; row < this.board.getSize(); row++) {
            //We can only solve 2 empties
            if (this.board.countTypeInRow(row, EMPTY) != 2) continue;
            //Look for a similar row
            ComparingRowLoop:
            for (int comparingRow = 0; comparingRow < this.board.getSize(); comparingRow++) {
                if (row == comparingRow) continue;
                if (this.board.countTypeInRow(comparingRow, EMPTY) > 0) continue; //We can compare to a full row only
                int firstEmpty = -1;
                for (int column = 0; column < this.board.getSize(); column++) {
                    if (this.board.getTileAt(row, column) == EMPTY) {
                        if (firstEmpty == -1) firstEmpty = column; //Found the first empty tile in the row
                    } else {
                        if (this.board.getTileAt(row, column) != this.board.getTileAt(comparingRow, column))
                            continue ComparingRowLoop;
                    }
                }
                //We know firstEmpty will have the correct value now because there are 2 empties in the row
                if (place) board.setTileAt(row, firstEmpty, this.board.getTileAt(comparingRow, firstEmpty).opposite());
                return new Coordinate[]{ new Coordinate(row, firstEmpty), new Coordinate(comparingRow, firstEmpty) };
            }
        }
        return null;
    }

    private Coordinate[] findUniqueColumn(boolean place) {
        for (int column = 0; column < this.board.getSize(); column++) {
            //We can only solve 2 empties
            if (this.board.countTypeInColumn(column, EMPTY) != 2) continue;
            //Look for a similar column
            ComparingColumnLoop:
            for (int comparingColumn = 0; comparingColumn < this.board.getSize(); comparingColumn++) {
                if (column == comparingColumn) continue;
                if (this.board.countTypeInColumn(comparingColumn, EMPTY) > 0) continue; //We can compare to a full column only
                int firstEmpty = -1;
                for (int row = 0; row < this.board.getSize(); row++) {
                    if (this.board.getTileAt(row, column) == EMPTY) {
                        if (firstEmpty == -1) firstEmpty = row; //Found the first empty tile in the column
                    } else {
                        if (this.board.getTileAt(row, column) != this.board.getTileAt(row, comparingColumn))
                            continue ComparingColumnLoop;
                    }
                }
                //We know firstEmpty will have the correct value now because there are 2 empties in the column
                if (place) board.setTileAt(firstEmpty, column, this.board.getTileAt(firstEmpty, comparingColumn).opposite());
                return new Coordinate[]{ new Coordinate(firstEmpty, column), new Coordinate(firstEmpty, comparingColumn)};
            }
        }
        return null;
    }
}

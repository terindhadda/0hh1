package cas.se3xa3.bitsplease.model;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created on 25/10/2015.
 */
public class Board {
    private int size;
    private Row[] rows;

    public Board(int size) {
        this.size = size;
        this.rows = new Row[size];
        IntStream.range(0, size).forEach(i -> rows[i] = new Row(size));
    }

    /**
     * Get the size of this column.
     * @return the size of the column.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * A convenience method for getting the largest index that can be queried
     * @return {@link Board#getSize()} - 1
     */
    public int getMaxIndex() {
        return size - 1;
    }

    /**
     * Utility method to check that coords are on the board
     * @param row the row position
     * @param column the column position
     */
    private void checkInRange(int row, int column) {
        if (row < 0) throw new IllegalArgumentException("row must be >= 0 but was "+row);
        if (row >= size) throw new IllegalArgumentException("row must be < board size ("+size+") but was "+row);
        if (column < 0) throw new IllegalArgumentException("column must be >= 0 but was "+column);
        if (column >= size) throw new IllegalArgumentException("column must be < board size ("+size+") but was "+column);
    }

    /**
     * Get the tile at the given coordinates
     * @param row the row of the tile being queried.
     * @param column the column of the tile being queried.
     * @return the tile at the given index
     * @throws IllegalArgumentException if row or column doesn't fall in the range {@code [0, size)}
     * @throws TileLockedException if the position {@code (row, column)} has the locked flag set to true
     */
    public Tile getTileAt(int row, int column) {
        checkInRange(row, column);
        return rows[row].getTileAt(column);
    }

    /**
     * Set the tile at {@code (row, column)} to {@code newState}.
     * @param row the row of the tile being queried.
     * @param column the column of the tile being queried.
     * @param newState the new state of the tile at {@code (row, column)}.
     * @return the old state of the tile that was updated. See {@link Board#getTileAt(int, int)}
     * @throws IllegalArgumentException if row or column doesn't fall in the range {@code [0, size)}
     * @throws TileLockedException if the position {@code (row, column)} has the locked flag set to true
     */
    public Tile setTileAt(int row, int column, Tile newState) {
        checkInRange(row, column);
        Tile oldState = rows[row].getTileAt(column);
        rows[row].setTileAt(column, newState);
        return oldState;
    }

    /**
     * Set the tile at position {@code (row, column)} to it's {@link Tile#nextState()}.
     * @param row the row of the tile being queried.
     * @param column the column of the tile being queried.
     * @return the new state of the tile that was updated.
     * @throws IllegalArgumentException if row or column doesn't fall in the range {@code [0, size)}
     * @throws TileLockedException if the position {@code (row, column)} has the locked flag set to true
     */
    public Tile stepTileAt(int row, int column) {
        checkInRange(row, column);
        return rows[row].stepTileAt(column);
    }

    /**
     * Set the locked flag to {@code locked} for the tile at the given index.
     * @param row the row of the tile to lock/unlock.
     * @param column the column of the tile to lock/unlock.
     * @param locked the new state of the flag, true for locked and false for unlocked.
     * @throws IllegalArgumentException if row or column doesn't fall in the range {@code [0, size)}
     */
    public void setLockAt(int row, int column, boolean locked) {
        checkInRange(row, column);
        rows[row].setLockAt(column, locked);
    }

    /**
     * Set the locked flag to {@code locked} for all tiles in this board.
     * @param locked the new state of the flag, true for locked and false for unlocked.
     */
    public void setAllLocks(final boolean locked) {
        Arrays.stream(rows).forEach(row -> row.setAllLocks(locked));
    }

    /**
     * Check if a tile is locked or not.
     * @param row the row of the tile to check the locked flag of.
     * @param column the column of the tile to check the locked flag of.
     * @return true if the tile at the position {@code (row, column)}  is locked,
     *          false if it is unlocked.
     * @throws IllegalArgumentException if row or column doesn't fall in the range {@code [0, size)}
     */
    public boolean isLocked(int row, int column) {
        checkInRange(row, column);
        return rows[row].isLocked(column);
    }

    /**
     * Count the number of tiles of type {@code tile} in the row at index {@code row}.
     * @param row the index of the row to count.
     * @param tile the type of tile to count.
     * @return the number of tiles of type {@code tile} in row {@code row}.
     * @throws IllegalArgumentException if {@code row} does not fall in the range {@code [0, size)}.
     */
    public int countTypeInRow(int row, Tile tile) {
        if (row < 0) throw new IllegalArgumentException("row must be >= 0 but was "+row);
        if (row >= size) throw new IllegalArgumentException("row must be < board size ("+size+") but was "+row);
        return IntStream.range(0, size)
                .reduce(0, (sum, i) -> (rows[row].getTileAt(i) == tile ? sum + 1 : sum));
    }

    /**
     * Count the number of tiles of type {@code tile} in the column at index {@code column}.
     * @param column the index of the column to count.
     * @param tile the type of tile to count.
     * @return the number of tiles of type {@code tile} in column {@code column}.
     * @throws IllegalArgumentException if {@code column} does not fall in the range {@code [0, size)}.
     */
    public int countTypeInColumn(int column, Tile tile) {
        if (column < 0) throw new IllegalArgumentException("column must be >= 0 but was "+column);
        if (column >= size) throw new IllegalArgumentException("column must be < board size ("+size+") but was "+column);
        return IntStream.range(0, size)
                .reduce(0, (sum, i) -> (rows[i].getTileAt(column) == tile ? sum + 1 : sum));
    }
    
    /**
     * Sets all unlocked tiles to empty.
     */
    public void clearAllUnlockedTiles() {
    	Arrays.stream(rows).forEach(Row::clearAllUnlockedTiles);
    }
    
    /**
     * @return true if this board does not contain any empty tiles, false otherwise.
     */
    public boolean isFull() {
        return !IntStream.range(0, size).anyMatch(i -> countTypeInRow(i, Tile.EMPTY) > 0);
    }

    /**
     * Evaluate how much of this board is solved.
     * @return the percentage solved rounded to the nearest percentage point. {@code [0, 100]}
     */
    public int percentageSolved() {
        int amtEmpty = IntStream.range(0, size).reduce(0, (numEmpty, rowIndex) -> numEmpty + countTypeInRow(rowIndex, Tile.EMPTY));
        int amtFull = (size * size) - amtEmpty;
        float percentFull = amtFull / ((float) (size * size));
        return Math.round(100 * percentFull);
    }

    /**
     * Make a copy of this board.
     * @return a fresh board with the same state as this board.
     */
    public Board copy() {
        Board clone = new Board(size);
        IntStream.range(0, size).forEach(index -> clone.rows[index] = rows[index].copy());
        return clone;
    }

    /**
     * Copy the contents of this board onto another board.
     * @param toFill the board to fill up
     * @throws IllegalArgumentException if the size of {@code toFill.}{@link Board#getSize()} != {@code this.}{@link Board#getSize()}
     */
    public void copyInto(Board toFill) {
        if (toFill.getSize() != this.size) throw new IllegalArgumentException("Incompatible board sizes");
        IntStream.range(0, size * size)
                .forEach(coord -> toFill.setTileAt(coord % size, coord / size, rows[coord % size].getTileAt(coord / size)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return size == board.size && Arrays.equals(rows, board.rows);
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + Arrays.hashCode(rows);
        return result;
    }

    @Override
    public String toString() {
        return "Board{" +
                "size=" + size +
                ", rows=" + Arrays.toString(rows) +
                '}';
    }
}

package cas.se3xa3.bitsplease.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created on 25/10/2015.
 */
public class Row {
    private final class RowEntry {
        public Tile state = Tile.EMPTY;
        public boolean locked = false;

        @Override
        public int hashCode() {
            int result = state.hashCode();
            result = 31 * result + (locked ? 1 : 0);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RowEntry rowEntry = (RowEntry) o;

            return locked == rowEntry.locked && state == rowEntry.state;
        }

        @Override
        public String toString() {
            return String.valueOf(locked ?
                    Character.toUpperCase(state.getSerializedChar())
                    : state.getSerializedChar());
        }
    }

    private int size;
    private RowEntry[] contents;

    /**
     * Create an empty row of dimension {@code size}.
     * @param size the length of the row.
     */
    Row(int size) {
        this.size = size;
        this.contents = new RowEntry[size];
        IntStream.range(0, size).forEach(i -> contents[i] = new RowEntry());
    }

    /**
     * A utility method for checking if the index is in the row
     * @param index the index to check
     */
    private void checkInRange(int index) {
        if (index < 0) throw new IllegalArgumentException("index must be >= 0 but was "+index);
        if (index >= size) throw new IllegalArgumentException("index must be < row size ("+size+") but was "+index);
    }

    /**
     * Get the tile at the given index
     * @param index the index of the tile being queried.
     * @return the tile at the given index
     * @throws IllegalArgumentException if index doesn't fall in the range {@code [0, size)}
     */
    public Tile getTileAt(int index) {
        checkInRange(index);
        return contents[index].state;
    }

    /**
     * Set the tile at the given {@code index} to {@code newState}.
     * @param index the index of the tile being updated.
     * @param newState the new state of the tile at {@code index}.
     * @return the old state of the tile that was updated. See {@link Row#getTileAt(int)}
     * @throws IllegalArgumentException if index doesn't fall in the range {@code [0, size)}
     * @throws TileLockedException if the index is locked and therefore cannot be updated.
     */
    public Tile setTileAt(int index, Tile newState) {
        checkInRange(index);
        if (contents[index].locked) throw new TileLockedException("Cannot set tile at "+index+" to "+newState.name());
        Tile oldState = contents[index].state;
        contents[index].state = newState;
        return oldState;
    }

    /**
     * Set the tile at position {@code index} to it's {@link Tile#nextState()}.
     * @param index the index of the tile being updated.
     * @return the new state of the tile that was updated.
     * @throws IllegalArgumentException if index doesn't fall in the range {@code [0, size)}
     * @throws TileLockedException if the index is locked and therefore cannot be updated.
     */
    public Tile stepTileAt(int index) {
        checkInRange(index);
        if (contents[index].locked) throw new TileLockedException("Cannot step tile at "+index+".");
        return contents[index].state = contents[index].state.nextState();
    }

    /**
     * Get the size of this row.
     * @return the size of the row.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Set the locked flag to {@code locked} for the tile at the given index.
     * @param index the index of the tile to lock/unlock.
     * @param locked the new state of the flag, true for locked and false for unlocked.
     * @throws IllegalArgumentException if index doesn't fall in the range {@code [0, size)}
     */
    public void setLockAt(int index, boolean locked) {
        checkInRange(index);
        contents[index].locked = locked;
    }

    /**
     * Set the locked flag to {@code locked} for all tiles in this row.
     * @param locked the new state of the flag, true for locked and false for unlocked.
     */
    public void setAllLocks(final boolean locked) {
        Arrays.stream(contents).forEach(entry -> entry.locked = locked);
    }

    /**
     * Check if a tile is locked or not.
     * @param index the index of the tile to check the locked flag of
     * @return true if the tile at {@code index} is locked, false if it is unlocked
     * @throws IllegalArgumentException if index doesn't fall in the range {@code [0, size)}
     */
    public boolean isLocked(int index) {
        checkInRange(index);
        return contents[index].locked;
    }
    
    /**
     * Sets all unlocked tiles to empty.
     */
    public void clearAllUnlockedTiles() {
    	Arrays.stream(contents)
    			.filter(rowEntry -> !rowEntry.locked)
    			.forEach(rowEntry -> rowEntry.state = Tile.EMPTY);
    }
    
    public Row copy() {
        Row clone = new Row(size);
        IntStream.range(0, size).forEach(index -> {
            clone.contents[index].state = contents[index].state;
            clone.contents[index].locked = contents[index].locked;
        });
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        return size == row.size && Arrays.equals(contents, row.contents);

    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + Arrays.hashCode(contents);
        return result;
    }

    @Override
    public String toString() {
        return "Row{" +
                "size=" + size +
                ", contents=" + Arrays.toString(contents) +
                '}';
    }
}

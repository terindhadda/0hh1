package cas.se3xa3.bitsplease.model;

/**
 * Created on 25/10/2015.
 */
public enum Tile {
    EMPTY('e'),
    RED('r'),
    BLUE('b');

    private char serializedChar;

    Tile(char serializedChar) {
        this.serializedChar = serializedChar;
    }

    /**
     * @return the next state this tile should take when
     * clicked.
     */
    public Tile nextState() {
        switch (this) {
            case EMPTY: return RED;
            case RED: return BLUE;
            default: return EMPTY;
        }
    }

    /**
     * NOTE: The opposite of {@link Tile#EMPTY} is {@link Tile#EMPTY}
     * @return the opposing tile color.
     */
    public Tile opposite() {
        switch (this) {
            case RED: return BLUE;
            case BLUE: return RED;
            default: return null;
        }
    }

    /**
     * @return the serialized representation of this tile
     */
    public char getSerializedChar() {
        return serializedChar;
    }

    /**
     * get the tile represented by {@code serializedChar}
     * @param serializedChar the serialized representation
     * @return the {@link Tile} represented by this char.
     */
    public static Tile deserialize(char serializedChar) {
        switch (serializedChar) {
            case 'e': return Tile.EMPTY;
            case 'r': return Tile.RED;
            case 'b': return Tile.BLUE;
            default:  return null;
        }
    }
}

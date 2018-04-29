package cas.se3xa3.bitsplease.model;

/**
 * Created on 25/10/2015.
 */
public class Coordinate {
    private int row;
    private int column;

    public Coordinate(int row, int column) {
        if (row < 0) throw new IllegalArgumentException("row must be > 0 but was "+row);
        if (column < 0) throw new IllegalArgumentException("column must be > 0 but was "+column);
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (row != that.row) return false;
        return column == that.column;

    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.row, this.column);
    }
}

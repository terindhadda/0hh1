package cas.se3xa3.bitsplease.model.checker;

import cas.se3xa3.bitsplease.model.Coordinate;
import cas.se3xa3.bitsplease.model.Tile;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static cas.se3xa3.bitsplease.model.Tile.*;
import static cas.se3xa3.bitsplease.model.checker.Result.State.*;

/**
 * Created on 25/10/2015.
 * A number of {@link Rule} implementations that return information about how
 * the rule is being violated.
 */
public class Rules {
    private static final Random r = new Random(System.currentTimeMillis());

    public static final Rule NO_EMPTY_TILES = board -> {
        Set<Coordinate> empties = new HashSet<>();
        IntStream.range(0, board.getSize()).forEach(row ->
            IntStream.range(0, board.getSize()).forEach(column -> {
                if (board.getTileAt(row, column) == EMPTY) empties.add(new Coordinate(row, column));
            })
        );
        if (empties.isEmpty()) return new Result(SATISFIES, "All tiles full", null);
        else                   return new Result(VIOLATES, "Board contains empty tiles", empties);
    };

    public static final Rule EQUAL_TILE_COUNT_ROW = board -> {
        boolean[] problemRows = new boolean[board.getSize()];
        IntStream.range(0, board.getSize()).forEach(row -> problemRows[row] = board.countTypeInRow(row, RED) > board.getSize() / 2
                || board.countTypeInRow(row, BLUE) > board.getSize() / 2);
        final int selection = r.nextInt(board.getSize());
        final OptionalInt problemRow = IntStream.range(selection, selection + board.getSize())
                .filter(i -> problemRows[i % board.getSize()])
                .findFirst();
        if (!problemRow.isPresent())
            return new Result(SATISFIES, "Rows have successful or potentially successful sums", null);
        Set<Coordinate> problemCoords = new HashSet<>();
        int row = problemRow.getAsInt() % board.getSize();
        IntStream.range(0, board.getSize())
                .forEach(column -> problemCoords.add(new Coordinate(row, column)));
        return new Result(VIOLATES, String.format("Row %d: Incorrect tile sum", row+1), problemCoords);
    };

    public static final Rule EQUAL_TILE_COUNT_COLUMN = board -> {
        boolean[] problemColumns = new boolean[board.getSize()];
        IntStream.range(0, board.getSize()).forEach(column -> problemColumns[column] = board.countTypeInColumn(column, RED) > board.getSize() / 2
                || board.countTypeInColumn(column, BLUE) > board.getSize() / 2);
        final int selection = r.nextInt(board.getSize());
        final OptionalInt problemColumn = IntStream.range(selection, selection + board.getSize())
                .filter(i -> problemColumns[i % board.getSize()])
                .findFirst();
        if (!problemColumn.isPresent())
            return new Result(SATISFIES, "Columns have successful or potentially successful sums", null);
        Set<Coordinate> problemCoords = new HashSet<>();
        int column = problemColumn.getAsInt() % board.getSize();
        IntStream.range(0, board.getSize())
                .forEach(row -> problemCoords.add(new Coordinate(row, column)));
        return new Result(VIOLATES, String.format("Column %d: Incorrect tile sum", column+1), problemCoords);
    };

    public static final Rule MAX_2_CONSECUTIVE_ROW = board -> {
        int startingRow = r.nextInt(board.getSize());
        Optional<Set<Coordinate>> problemRow = IntStream.range(startingRow, startingRow + board.getSize())
                .mapToObj((IntFunction<Set<Coordinate>>) row -> {
                    row = row % board.getSize();
                    Set<Coordinate> consecutiveRed = new HashSet<>();
                    Set<Coordinate> consecutiveBlue = new HashSet<>();
                    for (int column = 0; column < board.getSize(); column++) {
                        Tile tile = board.getTileAt(row, column);
                        switch (tile) {
                            case EMPTY:
                                if (consecutiveRed.size() > 2) return consecutiveRed;
                                else if (consecutiveBlue.size() > 2) return consecutiveBlue;
                                consecutiveRed.clear();
                                consecutiveBlue.clear();
                                break;
                            case RED:
                                if (consecutiveBlue.size() > 2) return consecutiveBlue;
                                consecutiveRed.add(new Coordinate(row, column));
                                consecutiveBlue.clear();
                                break;
                            case BLUE:
                                if (consecutiveRed.size() > 2) return consecutiveRed;
                                consecutiveRed.clear();
                                consecutiveBlue.add(new Coordinate(row, column));
                                break;
                        }
                    }
                    return new HashSet<>();
                })
                .filter(set -> !set.isEmpty())
                .findFirst();
        if (!problemRow.isPresent())
            return new Result(SATISFIES, "No rows contain more than 2 consecutive non-empty tiles", null);
        Coordinate badTile = problemRow.get().stream().findAny().get();
        Tile tile = board.getTileAt(badTile.getRow(), badTile.getColumn());
        return new Result(VIOLATES,
                String.format("Row %d: Too many consecutive %s in a row", badTile.getRow()+1, tile.name().toLowerCase()),
                problemRow.get());
    };

    public static final Rule MAX_2_CONSECUTIVE_COLUMN = board -> {
        int startingColumn = r.nextInt(board.getSize());
        Optional<Set<Coordinate>> problemColumn = IntStream.range(startingColumn, startingColumn + board.getSize())
                .mapToObj((IntFunction<Set<Coordinate>>) column -> {
                    column = column%board.getSize();
                    Set<Coordinate> consecutiveRed = new HashSet<>();
                    Set<Coordinate> consecutiveBlue = new HashSet<>();
                    for (int row = 0; row < board.getSize(); row++) {
                        Tile tile = board.getTileAt(row, column);
                        switch (tile) {
                            case EMPTY:
                                if (consecutiveRed.size() > 2) return consecutiveRed;
                                else if (consecutiveBlue.size() > 2) return consecutiveBlue;
                                consecutiveRed.clear();
                                consecutiveBlue.clear();
                                break;
                            case RED:
                                if (consecutiveBlue.size() > 2) return consecutiveBlue;
                                consecutiveRed.add(new Coordinate(row, column));
                                consecutiveBlue.clear();
                                break;
                            case BLUE:
                                if (consecutiveRed.size() > 2) return consecutiveRed;
                                consecutiveRed.clear();
                                consecutiveBlue.add(new Coordinate(row, column));
                                break;
                        }
                    }
                    return new HashSet<>();
                })
                .filter(set -> !set.isEmpty())
                .findFirst();
        if (!problemColumn.isPresent()) return new Result(SATISFIES, "No columns contain more than 2 consecutive non-empty tiles", null);
        Coordinate badTile = problemColumn.get().stream().findAny().get();
        Tile tile = board.getTileAt(badTile.getRow(), badTile.getColumn());
        return new Result(VIOLATES,
                String.format("Column %d: Too many consecutive %s in a column", badTile.getColumn()+1, tile.name().toLowerCase()),
                problemColumn.get());
    };

    //TODO Rule: Identical row/column only considers full rows. Look into odd (maybe just "1"?) tile numbers left
    public static final Rule NO_IDENTICAL_ROWS = board -> {
        int startingRow = r.nextInt(board.getSize());
        OptionalLong rowPair = IntStream.range(startingRow, startingRow + board.getSize())
                .map(i -> i % board.getSize())
                .mapToLong((row) -> {
                    int startingCompRow = r.nextInt(board.getSize());
                    OptionalInt matchingRow = IntStream.range(startingCompRow, startingCompRow + board.getSize())
                            .map(i -> i % board.getSize())
                            .filter(compRow ->
                                            IntStream.range(0, board.getSize()).allMatch(column -> row != compRow
                                                    && board.getTileAt(row, column) != EMPTY
                                                    && board.getTileAt(row, column) == board.getTileAt(compRow, column))
                            ).findFirst();
                    if (matchingRow.isPresent() && matchingRow.getAsInt() != row)
                        return ((long) row) << 32 | matchingRow.getAsInt();
                    else return Long.MIN_VALUE;
                }).filter(pair -> pair != Long.MIN_VALUE).findFirst();
        if (!rowPair.isPresent()) return new Result(SATISFIES, "No identical rows", null);
        int row1 = (int) (rowPair.getAsLong() >> 32);
        int row2 = (int) (rowPair.getAsLong() & 0xFFFF);
        Set<Coordinate> coords = new HashSet<>();
        IntStream.range(0, board.getSize()).forEach(column -> {
            coords.add(new Coordinate(row1, column));
            coords.add(new Coordinate(row2, column));
        });
        return new Result(VIOLATES, String.format("Row %d & Row %d: Identical", row1+1, row2+1), coords);
    };

    public static final Rule NO_IDENTICAL_COLUMNS = board -> {
        int startingColumn = r.nextInt(board.getSize());
        OptionalLong columnPair = IntStream.range(startingColumn, startingColumn + board.getSize())
                .map(i -> i%board.getSize())
                .mapToLong((column) -> {
                    int startingCompColumn = r.nextInt(board.getSize());
                    OptionalInt matchingColumn = IntStream.range(startingCompColumn, startingCompColumn + board.getSize())
                            .map(i -> i%board.getSize())
                            .filter(compColumn ->
                                            IntStream.range(0, board.getSize()).allMatch(row -> column != compColumn
                                                    && board.getTileAt(row, column) != EMPTY
                                                    && board.getTileAt(row, column) == board.getTileAt(row, compColumn))
                            ).findFirst();
                    if (matchingColumn.isPresent() && matchingColumn.getAsInt() != column)
                        return ((long) column)<<32 | matchingColumn.getAsInt();
                    else return Long.MIN_VALUE;
                }).filter(pair -> pair != Long.MIN_VALUE).findFirst();
        if (!columnPair.isPresent()) return new Result(SATISFIES, "No identical columns", null);
        int column1 = (int) (columnPair.getAsLong() >> 32);
        int column2 = (int) (columnPair.getAsLong() & 0xFFFF);
        Set<Coordinate> coords = new HashSet<>();
        IntStream.range(0, board.getSize()).forEach(row -> {
            coords.add(new Coordinate(row, column1));
            coords.add(new Coordinate(row, column2));
        });
        return new Result(VIOLATES, String.format("Column %d & Column %d: Identical", column1+1, column2+1), coords);
    };
}

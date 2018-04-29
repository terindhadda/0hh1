package cas.se3xa3.bitsplease.model.generator;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Tile;
import cas.se3xa3.bitsplease.model.checker.SimpleRules;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created on 01/11/2015.
 * Generates solved boards. Each solved board returned in consecutive calls
 * to {@link SolvedBoardGenerator#generateBoard()} is unique from the last
 * {@link SolvedBoardGenerator#UNIQUENESS_LOOKBEHIND} boards returned.
 */
public class SolvedBoardGenerator {
    public static final int UNIQUENESS_LOOKBEHIND = 8;

    private int size;

    private Queue<Board> previousBoards;

    public SolvedBoardGenerator(int size) {
        this.size = size;
        previousBoards = new LinkedList<>();
    }

    /**
     * Generates a solved board of the size of this generator.
     * @return the craeted board
     */
    public Board generateBoard() {
        Board board = new Board(size);
        LinkedList<Integer> validRows = generateRows();
        Collections.shuffle(validRows);
        int rowIndex = 0;
        int[] rowsUsed = new int[size];
        int[] attempts = new int[size];
        do {
            attempts[rowIndex]++;
            int row = validRows.poll();
            setRow(rowIndex, row, board);
            if (isValid(board)) {
                rowsUsed[rowIndex] = row;
                rowIndex++;
            } else {
                validRows.offer(row);
                emptyRow(rowIndex, board);
                if (rowsUsed[rowIndex] != 0) {
                    validRows.offer(rowsUsed[rowIndex]);
                    rowsUsed[rowIndex] = 0;
                }
                if (attempts[rowIndex] >= validRows.size()) {
                    attempts[rowIndex] = 0;
                    for (int clearRowIndex = 1; clearRowIndex < rowIndex; clearRowIndex++) {
                        if (rowsUsed[clearRowIndex] != 0) {
                            validRows.offer(rowsUsed[clearRowIndex]);
                            rowsUsed[clearRowIndex] = 0;
                        }
                        emptyRow(clearRowIndex, board);
                        attempts[clearRowIndex] = 0;
                    }
                    rowIndex = 1;
                }
            }
        }
        while (rowIndex < size);
        previousBoards.offer(board);
        if (previousBoards.size() > UNIQUENESS_LOOKBEHIND) previousBoards.poll();
        return board;
    }

    private LinkedList<Integer> generateRows() {
        //Take all values a binary number with "size" bits can have
        //and filter out the invalid combinations
        return IntStream.range(0, (int) Math.pow(2, size))
                .filter(this::isValidRow)
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private boolean isValidRow(int vector) {
        //Get all coordinates of the 1's in the vector
        int[] ones = IntStream.range(0, size)
                .sequential()
                .filter(bit -> {
                    int shiftFactor = size - 1 - bit;
                    return (vector & (1 << shiftFactor)) != 0;
                })
                .toArray();
        //Get all the coordinates of the 0's in the vector
        int[] zeros = IntStream.range(0, size)
                .sequential()
                .filter(bit -> {
                    int shiftFactor = size - 1 - bit;
                    return (vector & (1 << shiftFactor)) == 0;
                })
                .toArray();
        //Make sure there are not too many or too few 1's
        if (ones.length != this.size/2) return false;
        //Ensure that there are a max of 2 0's between 1's
        int previousBit = -1;
        for (int bit : ones) {
            if (bit - previousBit > 3) return false;
            previousBit = bit;
        }
        //Ensure that there are a max of 2 1's between 0's
        previousBit = -1;
        for (int bit : zeros) {
            if (bit - previousBit > 3) return false;
            previousBit = bit;
        }
        return true;
    }

    private boolean isValid(Board board) {
        return SimpleRules.EQUAL_BLUE_AND_RED.holds(board)
                && SimpleRules.NO_THREE_IN_A_ROW.holds(board)
                && (!board.isFull() || SimpleRules.NO_IDENTICAL_ROWS_OR_COLUMNS.holds(board))
                && !previousBoards.contains(board);
    }

    private void setRow(int rowIndex, int rowContents, Board board) {
        IntStream.range(0, size).forEach(columnIndex ->
                board.setTileAt(rowIndex, columnIndex, (rowContents & (1 << (size - 1 - columnIndex))) == 0 ? Tile.RED : Tile.BLUE));
    }

    private void emptyRow(int rowIndex, Board board) {
        IntStream.range(0, size).forEach(columnIndex -> board.setTileAt(rowIndex, columnIndex, Tile.EMPTY));
    }
}

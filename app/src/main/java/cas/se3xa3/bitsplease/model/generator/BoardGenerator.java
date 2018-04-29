package cas.se3xa3.bitsplease.model.generator;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Coordinate;
import cas.se3xa3.bitsplease.model.Tile;
import cas.se3xa3.bitsplease.model.solver.BoardSolver;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created on 30/10/2015.
 * Generates solvable boards via the {@link BoardGenerator#generate(int)} method.
 */
public class BoardGenerator {
    /**
     * Returned boards cannot be more solved than this.
     */
    private static final int MAX_SOLVED_THRESHOLD = 40;

    /**
     * The max number of attempts to achieve the desired quality.
     */
    private static final int ATTEMPTS_FOR_QUALITY = 32;

    /**
     * The max number of attempts to remove another tile before giving up.
     */
    private static final int ATTEMPTS_TO_SOLVE = 6;

    private SolvedBoardGenerator[] solvedBoardGenerators;
    private Random random = new Random(System.currentTimeMillis());

    public BoardGenerator() {
        solvedBoardGenerators = new SolvedBoardGenerator[5];
        IntStream.of(4, 6, 8, 10, 12)
                .forEach(size -> solvedBoardGenerators[(size / 2) - 2] = new SolvedBoardGenerator(size));
    }

    private SolvedBoardGenerator getGenerator(int size) {
        return solvedBoardGenerators[(size / 2) - 2];
    }

    /**
     * Check if the generator can generate a board of the given size.
     * @param size the size to check.
     * @return true if this generator can generate a board of size {@code size}.
     */
    public boolean isAcceptedSize(int size) {
        return !(size < 0 || size%2 == 1 || ((size / 2) - 2) > solvedBoardGenerators.length);
    }

    /**
     * Generate a board of size {@code size}.
     * @param size the size of the generated board.
     * @return a starter puzzle that can be solved with the starting tiles locked.
     * See {@link Board#isLocked(int, int)} to check if a tile is locked.<br>
     * See {@link BoardSolver} for a board solver implementation.<br>
     * @throws IllegalArgumentException if !{@link BoardGenerator#isAcceptedSize(int)}.
     */
    public Board generate(int size) {
        if (!isAcceptedSize(size)) throw new IllegalArgumentException("Invalid size. " + size);
        //Generate a new solved board.
        Board solvedBoard = getGenerator(size).generateBoard();
        //Take pieces away until it is acceptable
        Board bestBoard = null;
        Queue<Coordinate> coordsToPull = null;
        int attempts = 0;
        while (attempts++ < ATTEMPTS_FOR_QUALITY) {
            Board workingBoard = solvedBoard.copy();
            BoardSolver solver = new BoardSolver(workingBoard);
            coordsToPull = buildCoordsToPull(size);
            Set<Coordinate> removed = new HashSet<>();
            int failedAttempts = 0;
            while(!coordsToPull.isEmpty() && failedAttempts < ATTEMPTS_TO_SOLVE) {
                Coordinate toErase = coordsToPull.poll();
                Tile removedTile = workingBoard.setTileAt(toErase.getRow(), toErase.getColumn(), Tile.EMPTY);
                if (solver.tryToSolve()) {
                    //It is solvable, take all the removed tiles back
                    removed.add(toErase);
                    restore(removed, workingBoard);
                    failedAttempts = 0;
                } else {
                    //Not solvable, put the piece back
                    restore(removed, workingBoard);
                    workingBoard.setTileAt(toErase.getRow(), toErase.getColumn(), removedTile);
                    failedAttempts++;
                }
            }
            //Is this board good enough to stop?
            if (workingBoard.percentageSolved() < MAX_SOLVED_THRESHOLD) {
                //We did it!
                bestBoard = workingBoard;
                break;
            }
            //Nope, it might be the best we have done though
            if (bestBoard == null || bestBoard.percentageSolved() < workingBoard.percentageSolved()) {
                bestBoard = workingBoard;
            }
        }
        lockRemaining(bestBoard);
        return bestBoard;
    }

    private Queue<Coordinate> buildCoordsToPull(int size) {
        LinkedList<Coordinate> coords = new LinkedList<>();
        IntStream.range(0, size * size)
                .forEach(coordData -> coords.offer(new Coordinate(coordData % size, coordData / size)));
        Collections.shuffle(coords);
        return coords;
    }

    private void lockRemaining(Board board) {
        IntStream.range(0, board.getSize() * board.getSize())
                .filter(coordData -> board.getTileAt(coordData % board.getSize(), coordData / board.getSize()) != Tile.EMPTY)
                .forEach(coordData -> board.setLockAt(coordData % board.getSize(), coordData / board.getSize(), true));
    }

    private void restore(Set<Coordinate> empties, Board board) {
        empties.stream().forEach(coord -> board.setTileAt(coord.getRow(), coord.getColumn(), Tile.EMPTY));
    }
}

package cas.se3xa3.bitsplease.model.generator;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Tile;
import cas.se3xa3.bitsplease.model.solver.BoardSolver;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

/**
 * Created on 16/11/2015.
 */
public class BoardGeneratorFunctionalTest2 {
    private BoardGenerator generator;

    public BoardGeneratorFunctionalTest2() {
        generator = new BoardGenerator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInput() {
        generator.generate(3);
    }

    @Test
    public void testUniqueness() {
        Queue<Board> lastBoards = new LinkedList<>();
        for (int testNo = 0; testNo < 100; testNo++) {
            Board board = generator.generate(4);
            assertTrue("Board was the same as a previously generated board.", !lastBoards.contains(board));
            lastBoards.offer(board);
            if (lastBoards.size() > SolvedBoardGenerator.UNIQUENESS_LOOKBEHIND)
                lastBoards.poll();
        }
    }

    /*
    @Test
    public void testMinimal() {
        Board board = generator.generate(12);
        board.setAllLocks(false);
        Board original = board.copy();
        System.out.println(original);
        BoardSolver solver = new BoardSolver(board);
        IntStream.range(0, 144).forEach(coorData -> {
            int row = coorData / 12;
            int column = coorData % 12;
            if (original.getTileAt(row, column) != Tile.EMPTY) {
                original.copyInto(board);
                board.setTileAt(row, column, Tile.EMPTY);
                assertTrue("Board is solvable when removing (" + row + ", " +column+").", !solver.tryToSolve());
            }
        });
    }
    s*/
}

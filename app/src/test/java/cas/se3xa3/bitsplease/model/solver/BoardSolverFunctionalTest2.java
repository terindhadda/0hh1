package cas.se3xa3.bitsplease.model.solver;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.GsonWrapper;
import org.junit.Test;
import static cas.se3xa3.bitsplease.model.Tile.*;
import static org.junit.Assert.assertEquals;

/**
 * Created on 25/10/2015.
 * These tests did not fit the parameter spec and required their own test
 */
public class BoardSolverFunctionalTest2 {
    private static final GsonWrapper GSON = new GsonWrapper(true);

    //Functional - 1
    @Test
    public void testSolveBoard() throws Exception {
        Board testBoard = new Board(4);
        int[][] reds = new int[][] {
                {1, 1},
                {2, 1},
                {2, 2},
                {3, 1},
                {3, 2}
        };
        for (int[] coords : reds) testBoard.setTileAt(coords[0], coords[1], RED);
        BoardSolver solver = new BoardSolver(testBoard);
        solver.tryToSolve();
        System.out.println("--");
        System.out.println(GSON.getGson().toJson(testBoard));
        for (int[] coords : reds)
            assertEquals(RED, testBoard.getTileAt(coords[0], coords[1]));
    }
}

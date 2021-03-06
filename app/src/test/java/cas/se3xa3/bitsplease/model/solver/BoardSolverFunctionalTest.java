package cas.se3xa3.bitsplease.model.solver;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.GsonWrapper;
import com.google.gson.stream.JsonReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created on 25/10/2015.
 */
@RunWith(Parameterized.class)
public class BoardSolverFunctionalTest {
    private static final GsonWrapper GSON = new GsonWrapper(true);

    private Board testBoard;
    private Board expectedBoard;

    public BoardSolverFunctionalTest(Board testBoard, Board expectedBoard) {
        this.testBoard = testBoard;
        this.expectedBoard = expectedBoard;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestBoards() throws IOException {
        GsonWrapper wrapper = new GsonWrapper(false);
        JsonReader reader = wrapper.wrapReader(new BufferedReader(new InputStreamReader(
                BoardSolverFunctionalTest.class.getClassLoader().getResourceAsStream("solver/BoardSolverFunctionalTestParams.json")
        )));
        try {
            Board[][] params = wrapper.getGson().fromJson(reader, Board[][].class);
            return Arrays.asList(params);
        } finally {
            reader.close();
        }
    }

    @Test
    public void testSolveBoard() throws Exception {
        BoardSolver solver = new BoardSolver(testBoard);
        solver.tryToSolve();
        System.out.println("--");
        System.out.println(GSON.getGson().toJson(testBoard));
        assertEquals(expectedBoard, testBoard);
    }
}

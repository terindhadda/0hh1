package cas.se3xa3.bitsplease.model.generator;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.checker.BoardChecker;
import cas.se3xa3.bitsplease.model.checker.Result;
import cas.se3xa3.bitsplease.model.solver.BoardSolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 16/11/2015.
 */
@RunWith(Parameterized.class)
public class BoardGeneratorFunctionalTest {
    private int size;
    private BoardGenerator generator;

    public BoardGeneratorFunctionalTest(int size) {
        this.size = size;
        generator = new BoardGenerator();
    }

    @Parameterized.Parameters
    public static Collection<Integer[]> getTestData() {
        return IntStream.range(4, 13)
                .filter(size -> size % 2 == 0)
                .boxed()
                .map(size -> new Integer[]{size})
                .collect(Collectors.toList());
    }

    @Test
    public void testGenerate() throws Exception {
        System.out.printf("Testing size %d\n", size);

        Board board = generator.generate(size);
        assertEquals("Generated board should be of size "+size+" but was "+board.getSize(),
                size,
                board.getSize());

        BoardChecker checker = new BoardChecker(board);
        assertEquals("Generated board is not valid.",
                checker.isValid().getResultState(),
                Result.State.SATISFIES);

        BoardSolver solver = new BoardSolver(board);
        assertTrue("Solver could not solve the generated board.",
                solver.tryToSolve());
    }
}
package cas.se3xa3.bitsplease.model.checker;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.GsonWrapper;
import com.google.gson.stream.JsonReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Created on 28/10/2015.
 */
@RunWith(Parameterized.class)
public class BoardCheckerFunctionalTest {
    private boolean checkIsValid;
    private Board testBoard;
    private Set<Result> result;

    public BoardCheckerFunctionalTest(boolean checkIsValid, Board testBoard, Set<Result> result) {
        this.checkIsValid = checkIsValid;
        this.testBoard = testBoard;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData() throws IOException {
        GsonWrapper wrapper = new GsonWrapper(false);
        JsonReader reader = wrapper.wrapReader(new BufferedReader(new InputStreamReader(
                BoardCheckerStructuralTest.class.getClassLoader().getResourceAsStream("checker/BoardCheckerFunctionalTestParams.json")
        )));
        try {
            BoardCheckerFunctionalTest[] params = wrapper.getGson().fromJson(reader, BoardCheckerFunctionalTest[].class);
            return Arrays.stream(params)
                    .map(param -> new Object[]{param.checkIsValid, param.testBoard, param.result})
                    .collect(Collectors.toList());
        } finally {
            reader.close();
        }
    }

    @Test
    public void testIsSolved() throws Exception {
        BoardChecker checker = new BoardChecker(testBoard);
        Result result = (checkIsValid ? checker.isValid() : checker.isSolved());
        System.out.println("--");
        System.out.println(result.getMessage());
        System.out.println(result.getErrorCoords());
        assertTrue(this.result.stream().anyMatch(r -> r.getResultState() == result.getResultState()
                && (r.getErrorCoords() == result.getErrorCoords()
                || r.getErrorCoords().equals(result.getErrorCoords()))));
    }
}

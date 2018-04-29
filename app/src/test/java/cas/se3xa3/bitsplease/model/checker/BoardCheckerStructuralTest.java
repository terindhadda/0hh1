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
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created on 26/10/2015.
 */
@RunWith(Parameterized.class)
public class BoardCheckerStructuralTest {
    private Board testBoard;
    private Result result;

    public BoardCheckerStructuralTest(Board testBoard, Result result) {
        this.testBoard = testBoard;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Object> getTestInputs() throws IOException {
        GsonWrapper wrapper = new GsonWrapper(false);
        JsonReader reader = wrapper.wrapReader(new BufferedReader(new InputStreamReader(
                BoardCheckerStructuralTest.class.getClassLoader().getResourceAsStream("checker/BoardCheckerStructuralTestParams.json")
        )));
        try {
            BoardCheckerStructuralTest[] params = wrapper.getGson().fromJson(reader, BoardCheckerStructuralTest[].class);
            return Arrays.stream(params)
                    .map(param -> new Object[]{param.testBoard, param.result})
                    .collect(Collectors.toList());
        } finally {
            reader.close();
        }
    }

    @Test
    public void testIsValid() throws Exception {
        BoardChecker checker = new BoardChecker(testBoard);
        Result result = checker.isValid();
        System.out.println("--");
        System.out.println(result.getMessage());
        System.out.println(result.getErrorCoords());
        assertEquals(this.result.getResultState(), result.getResultState());
        assertEquals(this.result.getErrorCoords(), result.getErrorCoords());
    }
}
package cas.se3xa3.bitsplease.controller;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Timer;
import cas.se3xa3.bitsplease.model.checker.BoardChecker;
import cas.se3xa3.bitsplease.model.checker.Result;
import cas.se3xa3.bitsplease.model.checker.SimpleRules;
import cas.se3xa3.bitsplease.model.generator.BoardGenerator;
import cas.se3xa3.bitsplease.model.solver.BoardSolver;
import cas.se3xa3.bitsplease.model.solver.Hint;
import cas.se3xa3.bitsplease.view.BoardView;
import cas.se3xa3.bitsplease.view.GameBoard;

import java.awt.*;

/**
 * Created on 11/11/2015.
 */
public class GameSession implements TileClickedListener {
    private BoardGenerator generator;
    private Timer timer;
    private Board board;
    private GameBoard gameBoard;
    private BoardView boardView;
    private Validator validator;
    private BoardSolver solver;

    public GameSession() {
        generator = new BoardGenerator();
        timer = new Timer();
    }

    /**
     * Start a new game.
     * @param size the size of the board in this new game.
     */
    public void startNewGame(int size) {
        if (validator != null) validator.shutdown();
        board = generator.generate(size);
        gameBoard = new GameBoard(board);
        timer.restart();
        boardView = gameBoard.boardView();
        boardView.setBackground(Color.DARK_GRAY.darker().darker());
        boardView.addTileClickedListener(this);
        solver = new BoardSolver(board);
        gameBoard.addHintButtonListener(pressEvent -> {
            //Make sure we aren't passing an invalid board to the solver
            if (validator.isValid().getResultState() != Result.State.SATISFIES) return;
            Hint hint = solver.requestHint();
            gameBoard.setMessage(hint.getExplanation());
            if (hint.getTilesInvolved() != null)
                boardView.setHighlights(hint.getTilesInvolved());
        });
        validator = new Validator(board, gameBoard, this::onSolve);
        validator.start();
    }

    public GameBoard getView() {
        return this.gameBoard;
    }

    @Override
    public void onTileClick(int row, int col) {
        //The board is already solved
        if (validator.isSolved()) return;

        if (!board.isLocked(row, col)) {
            //The clicked tile can be changed
            board.stepTileAt(row, col);
            //Clear any existing highlights because the board has changed
            boardView.clearHighlights();
            gameBoard.resetMessage();
            //We want to mark the stop before the delayed check. If a win occurs
            //the end time will be now, not in the future.
            timer.markStop();
            //Request a validation check in the near future
            validator.schedule();
            //Update the view with the new state
            boardView.update();
        } else {
            //The clicked tile cannot be changed
            boardView.toggleDisplayLocked();
        }
    }

    private void onSolve() {
        //Lock the board and clean the surface
        board.setAllLocks(true);
        boardView.setDisplayLocked(false);
        boardView.clearHighlights();
        gameBoard.setMessage(String.format("You Win! Time: %.1fs", timer.getTotalTime() / 1000d));
    }
}

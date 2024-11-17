package gui;

import game.Board;
import game.GameLogic;
import game.Piece;
import javafx.scene.layout.GridPane;

public class BoardView extends GridPane implements GameObserver {
    private GameLogic gameLogic;
    private Cell[][] cells;

    public BoardView(GameLogic gameLogic){
        this.gameLogic = gameLogic;
        this.gameLogic.addObserver(this);
        cells = new Cell[Board.SIZE][Board.SIZE];
        initializeBoard();
    }

    @Override
    public void onGameStateChanged() {
        updateBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Cell cell = new Cell(row, col, gameLogic);
                cells[row][col] = cell;
                add(cell, col, row);
            }
        }
        updateBoard();
    }

    public void updateBoard() {
        Board board = gameLogic.getBoard();
        Piece[][] boardPieces = board.getBoard();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Piece piece = boardPieces[row][col];
                cells[row][col].updateCell(piece);
            }
        }
        // Destacar movimentos válidos
        highlightValidMoves();
    }

    private void highlightValidMoves() {
        Piece currentPlayer = gameLogic.getCurrentPlayer();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                try {
                    if (gameLogic.getBoard().isValidMove(row, col, currentPlayer)) {
                        cells[row][col].highlight(true);
                    } else {
                        cells[row][col].highlight(false);
                    }
                } catch (Exception e) {
                    cells[row][col].highlight(false);
                }
            }
        }
    }
}

package network;

import game.Board;

public class GameStateMessage extends Message {
    private Board board;

    public GameStateMessage(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}

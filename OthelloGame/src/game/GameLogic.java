package game;

import gui.GameObserver;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private Board board;
    private Piece currentPlayer;
    private List<GameObserver> observers;
    private boolean yourTurn;

    public GameLogic() {
        board = new Board();
        currentPlayer = Piece.BLACK; // Por padrão, o jogador preto começa
        observers = new ArrayList<>();
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    public boolean makeMove(int row, int col) throws Exception {
        if (board.isValidMove(row, col, currentPlayer)) {
            board.makeMove(row, col, currentPlayer);
            switchPlayer();
            notifyObservers();
            return true;
        }
        throw new Exception("Movimento inválido.");
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
    }

    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onGameStateChanged();
        }
    }

    public Piece getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        notifyObservers();
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }
    public void setCurrentPlayer(Piece currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public boolean isYourTurn(){
        return yourTurn;
    }

    public List<GameObserver> getObservers() {
        return observers;
    }

    public void setObservers(List<GameObserver> observers) {
        this.observers = observers;
    }

    public void setPlayerPiece(Piece playerPiece) {
        this.currentPlayer = playerPiece;
    }

}

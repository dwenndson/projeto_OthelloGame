package network;

import game.Piece;

public class TurnMessage extends Message {
    private boolean yourTurn;
    private Piece piece;

    public TurnMessage(Piece piece) {
        this.piece = piece;
    }

    public Piece getCurrentPlayer(){
        return piece;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }
}

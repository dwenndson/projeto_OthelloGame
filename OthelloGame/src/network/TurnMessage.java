package network;

public class TurnMessage extends Message {
    private boolean yourTurn;

    public TurnMessage(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }
}

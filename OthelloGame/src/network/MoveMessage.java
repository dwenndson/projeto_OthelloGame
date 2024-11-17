package network;

public class MoveMessage extends Message {
    private int row;
    private int col;

    public MoveMessage(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Getters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

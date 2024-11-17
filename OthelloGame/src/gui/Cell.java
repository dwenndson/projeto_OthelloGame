package gui;

import game.GameLogic;
import game.Piece;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import network.GameClient;

public class Cell extends StackPane {

    private int row;
    private int col;
    private Rectangle border;
    private Circle pieceView;
    private GameLogic gameLogic;
    private GameClient client;

    public Cell(int row, int col, GameLogic gameLogic) {
        this.row = row;
        this.col = col;
        this.gameLogic = gameLogic;

        border = new Rectangle(75, 75);
        border.setFill(Color.GREEN);
        border.setStroke(Color.BLACK);

        pieceView =  new Circle(30);
        pieceView.setVisible(false);

        setAlignment(Pos.CENTER);
        getChildren().addAll(border, pieceView);

        // Adicionar evento de clique
        setOnMouseClicked(event -> handleClick());
    }

    private void handleClick() {
        System.out.println("Célula clicada: (" + row + ", " + col + ")");
        try {
            Piece currentPlayer = gameLogic.getCurrentPlayer();
            if (gameLogic.getCurrentPlayer() == client.getPlayerPiece()) {
                client.sendMove(row, col);
            } else {
                System.out.println("Não é o seu turno.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao enviar movimento: " + e.getMessage());
        }
    }

    public void updateCell(Piece piece) {
        Platform.runLater(() -> {
            if (piece == Piece.BLACK) {
                pieceView.setFill(Color.BLACK);
                pieceView.setVisible(true);
            } else if (piece == Piece.WHITE) {
                pieceView.setFill(Color.WHITE);
                pieceView.setVisible(true);
            } else {
                pieceView.setVisible(false);
            }
        });
    }

    public void highlight(boolean highlight) {
        Platform.runLater(() -> {
            if (highlight) {
                border.setFill(Color.LIGHTGREEN);
            } else {
                border.setFill(Color.GREEN);
            }
        });
    }


}

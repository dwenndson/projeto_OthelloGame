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

    public Cell(int row, int col, GameLogic gameLogic, GameClient client) {
        this.row = row;
        this.col = col;
        this.gameLogic = gameLogic;
        this.client =  client;

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
        try {
            if (gameLogic.isYourTurn()) {
                // Verificar se o movimento é válido localmente
                if (gameLogic.getBoard().isValidMove(row, col, client.getPlayerPiece())) {
                    // Enviar o movimento para o servidor
                    client.sendMove(row, col);
                } else {
                    System.out.println("Movimento inválido.");
                }
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

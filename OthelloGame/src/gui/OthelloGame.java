package gui;

import game.GameLogic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.ChatMessage;
import network.GameClient;

public class OthelloGame extends Application {

    private GameLogic gameLogic;
    private BoardView boardView;
    private GameClient client;
    private TextArea chatArea;
    private TextField chatInput;
    private Button sendButton;
    private Label turnIndicator;

    private static final int BOARD_SIZE = 8;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        gameLogic = new GameLogic();

        client = new GameClient(this, gameLogic);

        boardView = new BoardView(gameLogic, client);

        turnIndicator  = new Label();
        updateTurnIndicator();

        VBox leftPane = new VBox(10, turnIndicator);

        // Configurar o chat
        setupChat();

        BorderPane root = new BorderPane();
        root.setCenter(boardView);
        root.setRight(createChatPane());
        root.setLeft(leftPane);

        // Configurar a cena
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Othello Game");
        primaryStage.show();

        new Thread(() -> {
            try {
                client = new GameClient(this, gameLogic);
                client.startClient("localhost");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void updateTurnIndicator(){
        Platform.runLater(() -> {
            String turnText = gameLogic.isYourTurn() ? "Seu turno" : "Turno do oponente";
            turnIndicator.setText(turnText);
        });
    }

    public void updateChat(ChatMessage chatMessage) {
        Platform.runLater(() -> {
            String sender = chatMessage.getSender();
            String message = chatMessage.getMessage();
            chatArea.appendText(sender + ": " + message + "\n");
        });
    }

    private void setupChat() {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        chatInput = new TextField();
        chatInput.setPromptText("Digite sua mensagem...");
        chatInput.setOnAction(e -> sendMessage());

        sendButton = new Button("Enviar");
        sendButton.setOnAction(e -> sendMessage());
    }

    private VBox createChatPane() {
        VBox chatPane = new VBox(5);
        chatPane.getChildren().addAll(chatArea, chatInput, sendButton);
        chatPane.setPrefWidth(200);
        return chatPane;
    }

    private void sendMessage() {
        String message = chatInput.getText();
        if (!message.isEmpty()) {
            client.sendChatMessage(message);
            chatInput.clear();
            chatArea.appendText("Você: " + message + "\n");
        }
    }
}

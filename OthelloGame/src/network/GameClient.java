package network;

import game.GameLogic;
import game.Piece;
import gui.OthelloGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient extends  NetworkConnection{
    private OthelloGame othelloGame;
    private GameLogic gameLogic;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private Piece playerPiece;
    public GameClient(OthelloGame othelloGame, GameLogic gameLogic) {
        this.othelloGame = othelloGame;
        this.gameLogic = gameLogic;
    }

    public Piece getPlayerPiece() {
        return playerPiece;
    }

    public void startClient(String serverIP) throws Exception {
        createConnection(serverIP);
        communicateWithServer();
    }


    public void sendChatMessage(String message) {
        try {
            ChatMessage chatMessage = new ChatMessage(playerPiece.toString(), message);
            out.writeObject(chatMessage);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int row, int col) {
        try {
            out.writeObject(new MoveMessage(row, col));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendQuit(){
        try {
            out.writeUTF("Quit");
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void createConnection(String serverIP) throws Exception {
        socket = new Socket(serverIP, 55555);
        System.out.println("Conectado ao servidor!");
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // Receber a peça do jogador do servidor
        TurnMessage turnMessage = (TurnMessage) in.readObject();
        playerPiece = turnMessage.getCurrentPlayer();
        gameLogic.setPlayerPiece(playerPiece);

        // Definir se é o seu turno inicial
        gameLogic.setYourTurn(playerPiece == Piece.BLACK); // Preto começa

        othelloGame.updateTurnIndicator();

        System.out.println("Você é o jogador com as peças: " + playerPiece);

        //Iniciar a comunicação com o servidor
        communicateWithServer();
    }

    private void communicateWithServer() throws Exception {
        while (true) {
            Message message = (Message) in.readObject();

            if (message instanceof ChatMessage) {
                ChatMessage chatMessage = (ChatMessage) message;
                othelloGame.updateChat(chatMessage);
            } else if (message instanceof GameStateMessage) {
                GameStateMessage gameStateMessage = (GameStateMessage) message;
                gameLogic.setBoard(gameStateMessage.getBoard());
            } else if (message instanceof TurnMessage) {
                TurnMessage turnMessage = (TurnMessage) message;
                gameLogic.setYourTurn(turnMessage.getCurrentPlayer() == playerPiece);
                othelloGame.updateTurnIndicator();
            } else if (message instanceof ErrorMessage) {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println("Erro: " + errorMessage.getError());
            }
        }
    }


    private Object getMoveFromGUI() {
        // Implementar a lógica para obter o movimento do jogador através da interface gráfica
        return null;
    }

    private void updateGameFromServer(Object data) {
        // Atualizar o estado do jogo na interface gráfica com base nos dados recebidos do servidor
    }

}
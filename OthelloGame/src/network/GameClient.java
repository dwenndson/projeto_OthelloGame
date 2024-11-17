package network;

import game.GameLogic;
import game.Piece;
import gui.OthelloGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient extends  NetworkConnection{
    private String serverIP;
    private OthelloGame othelloGame;
    private GameLogic gameLogic;
    private Piece playerPiece;

    public GameClient(){}
    public GameClient(OthelloGame othelloGame) {
        this.othelloGame = othelloGame;
    }

    public void sendChatMessage(String message) {
        try {
            out.writeObject(new ChatMessage(message));
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

    protected void createConnection() throws Exception {
        socket = new Socket(serverIP, 55555);
        System.out.println("Conectado ao servidor!");
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

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
                gameLogic.setYourTurn(turnMessage.isYourTurn());
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

    public void startClient(String localhost) throws Exception {
        createConnection();
        // Lógica para interagir com o servidor
    }

    public Piece getPlayerPiece() {
        return playerPiece;
    }
}

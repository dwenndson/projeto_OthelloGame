package network;

import game.GameLogic;
import game.Piece;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.out;

public class GameServer {
    private ServerSocket serverSocket;
    private GameLogic gameLogic;
    private Socket player1Socket;
    private Socket player2Socket;
    private ObjectOutputStream outPlayer1;
    private ObjectOutputStream outPlayer2;
    private ObjectInputStream inPlayer1;
    private ObjectInputStream inPlayer2;

    private void createConnection() throws Exception {
        serverSocket = new ServerSocket(55555);
        out.println("Servidor aguardando conexões...");

        // Aceitar conexão do jogador 1
        player1Socket = serverSocket.accept();
        out.println("Jogador 1 conectado!");
        outPlayer1 = new ObjectOutputStream(player1Socket.getOutputStream());
        inPlayer1 = new ObjectInputStream(player1Socket.getInputStream());

        // Aceitar conexão do jogador 2
        player2Socket = serverSocket.accept();
        out.println("Jogador 2 conectado!");
        outPlayer2 = new ObjectOutputStream(player2Socket.getOutputStream());
        inPlayer2 = new ObjectInputStream(player2Socket.getInputStream());

        // Iniciar o jogo
        manageGame();
    }

    private void manageGame() throws Exception {
        // Inicializar o jogo
        boolean gameRunning = true;
        boolean player1Turn = true;

        // Notificar os jogadores sobre quem é quem
        outPlayer1.writeObject(new TurnMessage(Piece.BLACK));
        outPlayer1.flush();
        outPlayer2.writeObject(new TurnMessage(Piece.WHITE));
        outPlayer2.flush();

        while (gameRunning) {
            if (player1Turn) {
                // Solicitar movimento do jogador 1
                outPlayer1.writeObject("Seu turno");
                Object move = inPlayer1.readObject();
                // Validar e aplicar o movimento
                // Enviar atualização para o jogador 2
                outPlayer2.writeObject(move);
            } else {
                // Solicitar movimento do jogador 2
                outPlayer2.writeObject(new TurnMessage(Piece.WHITE));
                outPlayer2.flush();
                Object move = inPlayer2.readObject();
                // Validar e aplicar o movimento
                // Enviar atualização para o jogador 1
                outPlayer1.writeObject(move);
            }

            // Alternar turno
            player1Turn = !player1Turn;

            new Thread(() -> handleClient(inPlayer1, outPlayer1, outPlayer2, true)).start();
            new Thread(() -> handleClient(inPlayer2, outPlayer2, outPlayer1, false)).start();
            // Verificar condições de término do jogo
            // Se o jogo terminar, definir gameRunning = false
        }

        // Fechar conexões
        closeConnections();
    }

    public void startServer() throws Exception {
        createConnection();
        // Lógica para gerenciar o jogo e o chat

        gameLogic = new GameLogic();

        // Iniciar o gerenciamento do jogo
        manageGame();
    }

    public void closeConnections() throws Exception {
        inPlayer1.close();
        inPlayer2.close();
        outPlayer1.close();
        outPlayer2.close();
        player1Socket.close();
        player2Socket.close();
    }

    public static void main(String[] args) {
        try {
            GameServer server = new GameServer();
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClient(ObjectInputStream in, ObjectOutputStream out, ObjectOutputStream outOtherClient, Boolean isPlayer1) {
        try {
            Piece playerPiece = isPlayer1 ? Piece.BLACK : Piece.WHITE;

            // Enviar mensagem inicial ao cliente indicando qual peça ele é
            out.writeObject(new TurnMessage(playerPiece));
            out.flush();

            while (true) {
                Message message = (Message) in.readObject();

                if (message instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) message;
                    // Repassar a mensagem de chat para o outro cliente
                    outOtherClient.writeObject(chatMessage);
                    outOtherClient.flush();
                } else if (message instanceof MoveMessage) {
                    MoveMessage moveMessage = (MoveMessage) message;
                    synchronized (gameLogic) {
                        if (gameLogic.getCurrentPlayer() == playerPiece) {
                            try {
                                if (gameLogic.makeMove(moveMessage.getRow(), moveMessage.getCol())) {
                                    // Enviar o estado atualizado para ambos os clientes
                                    GameStateMessage gameStateMessage = new GameStateMessage(gameLogic.getBoard());
                                    out.writeObject(gameStateMessage);
                                    out.flush();
                                    outOtherClient.writeObject(gameStateMessage);
                                    outOtherClient.flush();

                                    // Notificar os turnos
                                    out.writeObject(new TurnMessage(gameLogic.getCurrentPlayer()));
                                    out.flush();
                                    outOtherClient.writeObject(new TurnMessage(gameLogic.getCurrentPlayer()));
                                    outOtherClient.flush();
                                }
                            } catch (Exception e) {
                                out.writeObject(new ErrorMessage(e.getMessage()));
                                out.flush();
                            }
                        } else {
                            out.writeObject(new ErrorMessage("Não é o seu turno."));
                            out.flush();
                        }
                    }
                } else if (message instanceof ResignMessage) {
                    // Lidar com desistência
                    outOtherClient.writeObject(new ErrorMessage("O oponente desistiu. Você venceu!"));
                    outOtherClient.flush();
                    // Fechar conexões e encerrar threads
                    break;
                }
                // Outros tipos de mensagens, se houver
            }
        }catch (Exception e) {
            // O cliente desconectou
            System.out.println("Jogador " + (isPlayer1 ? "1" : "2") + " desconectou.");
            // Notificar o outro jogador
            try {
                outOtherClient.writeObject(new ErrorMessage("O oponente desconectou. Aguardando reconexão por 30 segundos."));
                outOtherClient.flush();
                // Iniciar timer de 30 segundos
                boolean reconnected = waitForReconnection(isPlayer1);
                if (!reconnected) {
                    outOtherClient.writeObject(new ErrorMessage("O oponente não reconectou. Você venceu!"));
                    outOtherClient.flush();
                    // Fechar conexões
                    outOtherClient.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private boolean waitForReconnection(boolean isPlayer) {

        return false;
    }

}

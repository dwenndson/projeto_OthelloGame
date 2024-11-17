package test.game;

import game.Board;
import game.Piece;

public class BoardTest {
    public static void main(String[] args) {
        Board board = new Board();

        try {
            // Teste 1: Movimento válido para BLACK em (2, 3)
            boolean isValid = board.isValidMove(2, 3, Piece.BLACK);
            System.out.println("Movimento em (2,3) é válido para BLACK? " + isValid);

            // Teste 2: Movimento inválido para WHITE em (2, 3)
            isValid = board.isValidMove(2, 3, Piece.WHITE);
            System.out.println("Movimento em (2,3) é válido para WHITE? " + isValid);

            // Teste 3: Posição fora dos limites
            isValid = board.isValidMove(-1, 0, Piece.BLACK);
            System.out.println("Movimento em (-1,0) é válido para BLACK? " + isValid);
        } catch (Exception e) {
            System.out.println("Exceção: " + e.getMessage());
        }
    }
}

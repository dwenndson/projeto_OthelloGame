package game;

public class Board {
    public static final int SIZE = 8;
    private Piece[][] board;

    public Board() {
        board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        // Configuração inicial das peças
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = Piece.EMPTY;
            }
        }
        board[3][3] = Piece.WHITE;
        board[3][4] = Piece.BLACK;
        board[4][3] = Piece.BLACK;
        board[4][4] = Piece.WHITE;
    }

    public boolean isValidMove(int row, int col, Piece playerPiece) throws Exception {
        if(row < 0 || row >= SIZE || col < 0 || col >= SIZE){
           throw new Exception("Posição fora dos limites do tabuleiro");
        }
        if (board[row][col] != null && board[row][col] != Piece.EMPTY) {
            throw new Exception("Posição já ocupada.");
        }
        Piece opponentPiece = (playerPiece == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        return directions(row, col, opponentPiece, playerPiece);
    }

    private boolean directions(int row, int col, Piece opponentPiece, Piece playerPiece){
        boolean isValid = false;

        int[] deltaRow = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] deltaCol = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int direction = 0; direction < 8; direction++) {
            int dRow = deltaRow[direction];
            int dCol = deltaCol[direction];
            int currentRow = row + dRow;
            int currentCol = col + dCol;
            boolean hasOpponentPieceBetween = false;

            // Avançar na direção atual
            while (currentRow >= 0 && currentRow < SIZE && currentCol >= 0 && currentCol < SIZE) {
                Piece currentPiece = board[currentRow][currentCol];
                if (currentPiece == opponentPiece) {
                    hasOpponentPieceBetween = true;
                } else if (currentPiece == playerPiece) {
                    if (hasOpponentPieceBetween) {
                        isValid = true;
                    }
                    break;
                } else {
                    break;
                }
                currentRow += dRow;
                currentCol += dCol;
            }
        }
        return isValid;
    }

    public void makeMove(int row, int col, Piece playerPiece) throws Exception {
        if (!isValidMove(row, col, playerPiece)) {
            throw new Exception("Movimento inválido.");
        }

        Piece opponentPiece = (playerPiece == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        board[row][col] = playerPiece;

        int[] deltaRow = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] deltaCol = {-1, 0, 1, 1, 1, 0, -1, -1};

        // Capturar peças do oponente em todas as direções válidas
        for (int direction = 0; direction < 8; direction++) {
            int dRow = deltaRow[direction];
            int dCol = deltaCol[direction];

            int r = row + dRow;
            int c = col + dCol;
            boolean hasOpponentPieceBetween = false;

            // Verificar se há peças do oponente na direção
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == opponentPiece) {
                r += dRow;
                c += dCol;
                hasOpponentPieceBetween = true;
            }

            // Se encontramos uma sequência válida, virar as peças
            if (hasOpponentPieceBetween && r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == playerPiece) {
                int rBack = row + dRow;
                int cBack = col + dCol;
                while (rBack != r || cBack != c) {
                    board[rBack][cBack] = playerPiece;
                    rBack += dRow;
                    cBack += dCol;
                }
            }
        }
    }


    public Piece[][] getBoard() {
        return board;
    }

    // Outros métodos úteis
}

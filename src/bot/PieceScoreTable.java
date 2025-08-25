package bot;

import chessMatch.ChessPieces.TypePiece;
import chessMatch.Color;
import chessMatch.Position;

public class PieceScoreTable {

    // Tabela para Peões (Brancos)
    private static final int[][] PAWN_TABLE = {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            {50, 50, 50, 50, 50, 50, 50, 50}, // Peões na 2ª fileira são valiosos
            {10, 10, 20, 30, 30, 20, 10, 10},
            { 5,  5, 10, 25, 25, 10,  5,  5},
            { 0,  0,  0, 20, 20,  0,  0,  0},
            { 5, -5,-10,  0,  0,-10, -5,  5},
            { 5, 10, 10,-20,-20, 10, 10,  5},
            { 0,  0,  0,  0,  0,  0,  0,  0}  // Peões na 1ª fileira não deveriam existir
    };

    // Tabela para Cavalos (Brancos)
    private static final int[][] KNIGHT_TABLE = {
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  5,  5,  0,-20,-40},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };

    // Tabela para Bispos (Brancos)
    private static final int[][] BISHOP_TABLE = {
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  5,  0,  0,  0,  0,  5,-10},
            {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20}
    };

    // Tabela para Torres (Brancas)
    private static final int[][] ROOK_TABLE = {
            { 0,  0,  0,  5,  5,  0,  0,  0},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            { 5, 10, 10, 10, 10, 10, 10,  5}, // Torres na 7ª fileira são fortes
            { 0,  0,  0,  0,  0,  0,  0,  0}
    };

    // Tabela para a Dama - geralmente bônus por centralização
    private static final int[][] QUEEN_TABLE = {
            {-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            { -5,  0,  5,  5,  5,  5,  0, -5},
            { -5,  0,  5,  5,  5,  5,  0, -5},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20}
    };

    // Tabela para o Rei (Meio-jogo) - Incentiva o roque e a segurança
    private static final int[][] KING_TABLE = {
            { 20, 30, 10,  0,  0, 10, 30, 20}, // Bônus para casas de roque
            { 20, 20,  0,  0,  0,  0, 20, 20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30}
    };

    public int getValue(TypePiece type, Color color, Position pos) {
        int row = pos.getRow();
        int col = pos.getColumn();

        if (color == Color.BLACK) {
            row = 7 - row;
        }

        return switch (type) {
            case PAWN -> PAWN_TABLE[row][col];
            case KNIGHT -> KNIGHT_TABLE[row][col];
            case BISHOP -> BISHOP_TABLE[row][col];
            case ROOK -> ROOK_TABLE[row][col];
            case QUEEN -> QUEEN_TABLE[row][col];
            case KING -> KING_TABLE[row][col];
            default -> 0;
        };
    }
}

package bot;

import engine.ChessPieces.TypePiece;
import engine.Color;
import engine.Position;

public class PieceScoreTable {

    private static final int[][] PAWN_TABLE = {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            { 5,  5, 10, 25, 25, 10,  5,  5},
            { 0,  0,  0, 20, 20,  0,  0,  0},
            { 5, -5,-10,  0,  0,-10, -5,  5},
            { 5, 10, 10,-20,-20, 10, 10,  5},
            { 0,  0,  0,  0,  0,  0,  0,  0}
    };

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

    private static final int[][] ROOK_TABLE = {
            { 0,  0,  0,  5,  5,  0,  0,  0},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            { 5, 10, 10, 10, 10, 10, 10,  5},
            { 0,  0,  0,  0,  0,  0,  0,  0}
    };

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

    private static final int[][] KING_TABLE = {
            { 20, 30, 10,  0,  0, 10, 30, 20},
            { 20, 20,  0,  0,  0,  0, 20, 20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30}
    };

    // Tables for endgame

    private static final int[][] KING_TABLE_EG = {
            {-50,-30,-30,-30,-30,-30,-30,-50},
            {-30,-10,  0,  0,  0,  0,-10,-30},
            {-30,  0, 20, 30, 30, 20,  0,-30},
            {-30,  0, 30, 40, 40, 30,  0,-30},
            {-30,  0, 30, 40, 40, 30,  0,-30},
            {-30,  0, 20, 30, 30, 20,  0,-30},
            {-30,-10,  0,  0,  0,  0,-10,-30},
            {-50,-30,-30,-30,-30,-30,-30,-50}
    };

    private static final int[][] PAWN_TABLE_EG = {
            {  0,   0,   0,   0,   0,   0,   0,   0},
            {100, 100, 100, 100, 100, 100, 100, 100},
            { 80,  80,  80,  80,  80,  80,  80,  80},
            { 50,  50,  50,  50,  50,  50,  50,  50},
            { 30,  30,  30,  30,  30,  30,  30,  30},
            { 20,  20,  20,  20,  20,  20,  20,  20},
            { 10,  10,  10,  10,  10,  10,  10,  10},
            {  0,   0,   0,   0,   0,   0,   0,   0}
    };

    private static final int[][] ROOK_TABLE_EG = {
            {10, 20, 20, 20, 20, 20, 20, 10},
            {20, 30, 30, 30, 30, 30, 30, 20},
            {20, 30, 30, 30, 30, 30, 30, 20},
            {20, 30, 30, 30, 30, 30, 30, 20},
            {20, 30, 30, 30, 30, 30, 30, 20},
            {20, 30, 30, 30, 30, 30, 30, 20},
            {80, 90, 90, 90, 90, 90, 90, 80},
            {10, 20, 20, 20, 20, 20, 20, 10}
    };

    public int getValue(TypePiece type, Color color, Position pos, double gamePhase) {
        int row = pos.getRow();
        int col = pos.getColumn();

        if (color == Color.BLACK) {
            row = 7 - row;
        }

        if (type == TypePiece.KING){
            int mgScore = KING_TABLE[row][col];
            int egScore = KING_TABLE_EG[row][col];
            return (int) (mgScore * gamePhase + egScore * (1 - gamePhase));
        }

        if (type == TypePiece.PAWN){
            int mgScore = PAWN_TABLE[row][col];
            int egScore = PAWN_TABLE_EG[row][col];
            return (int) (mgScore * gamePhase + egScore * (1 - gamePhase));
        }

        if (type == TypePiece.ROOK){
            int mgScore = ROOK_TABLE[row][col];
            int egScore = ROOK_TABLE_EG[row][col];
            return (int) (mgScore * gamePhase + egScore * (1 - gamePhase));
        }

        return switch (type) {
            case KNIGHT -> KNIGHT_TABLE[row][col];
            case BISHOP -> BISHOP_TABLE[row][col];
            case QUEEN -> QUEEN_TABLE[row][col];
            default -> 0;
        };
    }
}

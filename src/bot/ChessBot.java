package bot;

import chessMatch.ChessPieces.*;
import chessMatch.ChessRules;
import chessMatch.Color;
import chessMatch.Move;
import chessMatch.Piece;

import java.util.List;

public class ChessBot {

    private Color color;

    private ChessRules chessRules;

    public ChessBot(Color color, ChessRules chessRules) {
        this.color = color;
        this.chessRules = chessRules;
    }

    public Color getColor() {
        return color;
    }

    public Move findBestMove() {
        List<Move> possibleMoves = chessRules.possibleMoves(color);

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : possibleMoves) {

            Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());
            int score = findBestScore();
            chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }

        return bestMove;
    }

    private int findBestScore() {
        int score = 0;

        for (Piece piece : chessRules.getPiecesOnTheBoard()) {
            int pieceScore = switch (piece.getType()) {
                case PAWN -> 1;
                case KNIGHT -> 3;
                case BISHOP -> 3;
                case ROOK -> 5;
                case QUEEN -> 9;
                default -> 0;
            };
            score += piece.getColor() == color ? pieceScore : -pieceScore;
        }


        return score;
    }
}

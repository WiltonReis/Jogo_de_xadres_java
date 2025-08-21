package bot;

import chessMatch.*;
import chessMatch.ChessPieces.Knight;
import chessMatch.ChessPieces.Pawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Move> bestMoves = new ArrayList<>();

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : possibleMoves) {

            Piece movedPiece = move.getPieceMoved();

            Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());

            int score = findBestScore();

            if (movedPiece instanceof Pawn && movedPiece.getMoveCount() == 1) {
                if (move.getTarget().getColumn() == 3 || move.getTarget().getColumn() == 4) score += 50;
                else score += 25;
            }

            if (movedPiece instanceof Knight && movedPiece.getMoveCount() == 1){
                if (move.getTarget().getColumn() == 2 || move.getTarget().getColumn() == 5) score += 50;
                else score += 25;
            }

            chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

            if (score > bestScore) {
                bestMove = move;
                bestMoves.clear();
                bestMoves.add(bestMove);
                bestScore = score;
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        Random random = new Random();
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int findBestScore() {
        int score = 0;

        List<Piece> opponentPieces = chessRules.getPiecesOnTheBoard().stream()
                .filter(piece -> piece.getColor() != color)
                .toList();
        List<Move> opponentMoves = chessRules.possibleMoves(color == Color.WHITE ? Color.BLACK : Color.WHITE);

        for (Piece piece : chessRules.getPiecesOnTheBoard()) {

            int capturedPieceValue = 0;

            int pieceValue = switch (piece.getType()) {
                case PAWN -> 100;
                case KNIGHT, BISHOP -> 300;
                case ROOK -> 500;
                case QUEEN -> 900;
                default -> 0;
            };

            for (Piece opponentPiece : opponentPieces) {
                if (opponentPiece.possibleMove(piece.getPosition())) {
                    capturedPieceValue = pieceValue * 2;
                }
            }

            score += piece.getColor() == color ? pieceValue : -pieceValue;
            score -= capturedPieceValue;
        }

        return score;
    }
}

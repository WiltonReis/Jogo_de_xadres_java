package bot;

import chessMatch.*;
import chessMatch.ChessPieces.TypePiece;

import java.util.ArrayList;
import java.util.List;

public class Minimax {

    private static final int QUIESCENCE_MAX_DEPTH = 3;

    public static int minimax(ChessRules chessRules, int depth, int alpha, int beta, boolean isMaximizing) {

        //if (depth == 0) return chessRules.getBot().evaluateBoard();
        if (depth == 0) return quiescenceSearch(chessRules, QUIESCENCE_MAX_DEPTH, alpha, beta, isMaximizing);

        if (chessRules.getCheckmate() || chessRules.getDraw() || chessRules.getStalemate()){
            return chessRules.getBot().evaluateBoard();
        }

        List<Move> possibleMoves = chessRules.possibleMoves(isMaximizing ? Color.WHITE : Color.BLACK);

        possibleMoves.sort((a, b) -> Integer.compare(scoreMove(chessRules, b), scoreMove(chessRules, a)));

        if (isMaximizing){
            int maxScore = Integer.MIN_VALUE;

            for (Move move : possibleMoves){

               GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = minimax(chessRules, depth - 1, alpha, beta, false);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (beta <= alpha){
                    break;
                }
            }

            return maxScore;

        } else {
            int minScore = Integer.MAX_VALUE;

            for (Move move : possibleMoves){

                GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = minimax(chessRules, depth - 1, alpha, beta,true);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (beta <= alpha){
                    break;
                }

            }

            return minScore;
        }
    }

    private static int quiescenceSearch(ChessRules chessRules, int depth, int alpha, int beta, boolean isMaximizing) {

        int standScore = chessRules.getBot().evaluateBoard();

        if (depth <= 0 || chessRules.getCheckmate() || chessRules.getStalemate() || chessRules.getDraw())
            return standScore;

        if (isMaximizing){
            alpha = Math.max(alpha, standScore);
        } else beta = Math.min(beta, standScore);

        List<Move> captureMove = new ArrayList<>(chessRules.possibleMoves(isMaximizing ? Color.WHITE : Color.BLACK).stream().filter(move -> move.getCapturedPiece() != null).toList());
        captureMove.sort((a, b) -> Integer.compare(scoreMove(chessRules, b), scoreMove(chessRules, a)));

        if (isMaximizing){
            int maxScore = Integer.MIN_VALUE;

            for (Move move : captureMove){

                GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = quiescenceSearch(chessRules, depth - 1, alpha, beta, false);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (beta <= alpha){
                    break;
                }
            }

        } else {
            int minScore = Integer.MAX_VALUE;

            for (Move move : captureMove){

                GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = quiescenceSearch(chessRules, depth - 1, alpha, beta,true);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (beta <= alpha){
                    break;
                }

            }
        }

        return isMaximizing ? alpha : beta;
    }

    public static int scoreMove(ChessRules chessRules, Move move) {
        final int CAPTURE_SCORE = 5000;
        final int PROMOTION_SCORE = 10000;

        Piece targetPiece = chessRules.getBoard().piece(move.getTarget());

        if (move.getPieceMoved().getType() == TypePiece.PAWN && move.getTarget().getRow() == 0 || move.getTarget().getRow() == 7)
            return PROMOTION_SCORE;

        if (targetPiece != null)
            return CAPTURE_SCORE + (getPieceValue(targetPiece) - getPieceValue(move.getPieceMoved()));

        return 0;
    }

    private static int getPieceValue(Piece piece) {
        if (piece == null) return 0;
        return switch (piece.getType()) {
            case PAWN -> 100;
            case KNIGHT, BISHOP -> 300;
            case ROOK -> 500;
            case QUEEN -> 900;
            default -> 0;
        };
    }

}

package bot;

import chessMatch.*;
import chessMatch.ChessPieces.TypePiece;

import java.util.List;

public class Minimax {

    private static final int QUIESCENCE_MAX_DEPTH = 3;

    public static int minimax(ChessRules chessRules, int depth, int alpha, int beta, boolean isMaximizing) {


        if (depth == 0) return chessRules.getBot().evaluateBoard();
        //if (depth == 0) return quiescenceSearch(chessRules, depth, alpha, beta, isMaximizing);

        if (chessRules.getCheckmate() || chessRules.getDraw() || chessRules.getStalemate()){
            return chessRules.getBot().evaluateBoard();
        }

        List<Move> possibleMoves = chessRules.possibleMoves(isMaximizing ? Color.WHITE : Color.BLACK);

        possibleMoves.sort((a, b) -> Integer.compare(scoreMove(chessRules, b), scoreMove(chessRules, a)));

        if (isMaximizing){
            int maxScore = Integer.MIN_VALUE;

            int legalMovesFound = 0;

            for (Move move : possibleMoves){

               GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());


                int score = minimax(chessRules, depth - 1, alpha, beta, false);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                legalMovesFound++;

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (beta <= alpha){
                    break;
                }
            }

            return maxScore;

        } else {
            int minScore = Integer.MAX_VALUE;

            int legalMovesFound = 0;

            for (Move move : possibleMoves){

                GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = minimax(chessRules, depth - 1, alpha, beta,true);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);

                legalMovesFound++;

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (beta <= alpha){
                    break;
                }

            }

            return minScore;
        }
    }

    private static int quiescenceSearch(ChessRules chessRules, int depth, int alpha, int beta, boolean isMaximizing) {

        int standPat = chessRules.getBot().evaluateBoard();

        if (depth >= QUIESCENCE_MAX_DEPTH || chessRules.getCheckmate() || chessRules.getStalemate() || chessRules.getDraw()) return standPat;

        if (isMaximizing) {
            alpha = Math.max(standPat, alpha);
            if (alpha >= beta) return alpha;
        } else {
            beta = Math.min(standPat, beta);
            if (beta <= alpha) return beta;
        }

        List<Move> captureMove = chessRules.possibleMoves(isMaximizing ? Color.WHITE : Color.BLACK).stream().filter(move -> move.getCapturedPiece() != null).toList();
        captureMove.sort((a, b) -> Integer.compare(scoreMove(chessRules, b), scoreMove(chessRules, a)));

        for (Move move : captureMove){
            GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

            int score = quiescenceSearch(chessRules, depth +1, alpha, beta, !isMaximizing);

            chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

            if (isMaximizing){
                alpha = Math.max(alpha, score);
            } else {
                beta = Math.min(beta, score);
            }

            if (beta <= alpha){
                break;
            }
        }

        return isMaximizing ? alpha : beta;

    }

    public static int scoreMove(ChessRules chessRules, Move move) {
        int score = 0;
        Piece targetPiece = chessRules.getBoard().piece(move.getTarget());

        if (targetPiece != null) {
            score = 10 * getPieceValue(targetPiece) - getPieceValue(move.getPieceMoved());
        }

        if (move.getPieceMoved().getType() == TypePiece.PAWN) {
            if (move.getTarget().getRow() == 0 || move.getTarget().getRow() == 7) {
                score += 900;
            }
        }

        return score;
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

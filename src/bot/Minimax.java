package bot;

import chessMatch.ChessPieces.TypePiece;
import chessMatch.ChessRules;
import chessMatch.Color;
import chessMatch.Move;
import chessMatch.Piece;

import java.util.List;

public class Minimax {

    public static int minimax(ChessRules chessRules, int depth, int alpha, int beta, boolean isMaximizing, Color botColor) {

        if (depth == 0 || chessRules.getCheckmate() || chessRules.getDraw() || chessRules.getStalemate()){
            return chessRules.getBot().evaluateBoard();
        }

        List<Move> possibleMoves = chessRules.possibleMoves(isMaximizing ? botColor : chessRules.opponent(botColor));

        possibleMoves.sort((a, b) -> Integer.compare(scoreMove(chessRules, b), scoreMove(chessRules, a)));

        if (isMaximizing){
            int maxScore = Integer.MIN_VALUE;

            int legalMovesFound = 0;

            for (Move move : possibleMoves){

                boolean oldCheck = chessRules.getCheck();
                boolean oldCheckmate = chessRules.getCheckmate();
                boolean oldStalemate = chessRules.getStalemate();
                boolean oldDraw = chessRules.getDraw();
                Piece oldEnPassant = chessRules.getEnPassant();

                Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());


                int score = minimax(chessRules, depth - 1, alpha, beta, false, botColor);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                legalMovesFound++;

                chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

                chessRules.setCheck(oldCheck);
                chessRules.setCheckmate(oldCheckmate);
                chessRules.setStalemate(oldStalemate);
                chessRules.setDraw(oldDraw);
                chessRules.setEnPassant(oldEnPassant);

                if (beta <= alpha){
                    break;
                }
            }

            return maxScore;

        } else {
            int minScore = Integer.MAX_VALUE;

            int legalMovesFound = 0;

            for (Move move : possibleMoves){

                boolean oldCheck = chessRules.getCheck();
                boolean oldCheckmate = chessRules.getCheckmate();
                boolean oldStalemate = chessRules.getStalemate();
                boolean oldDraw = chessRules.getDraw();
                Piece oldEnPassant = chessRules.getEnPassant();

                Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = minimax(chessRules, depth - 1, alpha, beta,true, botColor);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);

                legalMovesFound++;

                chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

                chessRules.setCheck(oldCheck);
                chessRules.setCheckmate(oldCheckmate);
                chessRules.setStalemate(oldStalemate);
                chessRules.setDraw(oldDraw);
                chessRules.setEnPassant(oldEnPassant);

                if (beta <= alpha){
                    break;
                }

            }

            return minScore;
        }
    }

    private static int scoreMove(ChessRules chessRules, Move move) {
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

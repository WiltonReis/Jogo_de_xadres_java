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

        List<Integer> scores = new ArrayList<>();

        for (Move move : possibleMoves) {

            Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());

            int score = scoreBase() + movedScore(move) + (possibleMoves.size() * 2);
            scores.add(score);

            chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

        }

        int totalScore = scores.stream().mapToInt(Integer::intValue).sum();
        List<Double> probabilities = new ArrayList<>();

        for (int score : scores) {
            probabilities.add((double) score / totalScore);
        }

        Double random = Math.random();
        Double cumulativeProbability = 0.0;
        Move chosenMove = possibleMoves.get(0);

        for(int i = 0; i < probabilities.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (random <= cumulativeProbability) {
                chosenMove = possibleMoves.get(i);
                break;

            }
        }

        return chosenMove;
    }

    private int scoreBase() {
        int score = 0;

        for (Piece piece : chessRules.getPiecesOnTheBoard()) {

            int pieceValue = findPieceValue(piece);

            score += piece.getColor() == color ? pieceValue : -pieceValue;
        }

        return score;
    }

    private int movedScore(Move move) {
        int score = 0;

        Piece movedPiece = move.getPieceMoved();
        List<Position> possibleAttacks = chessRules.possibleAttacks(movedPiece);
        List<Piece> pieces = chessRules.getPiecesOnTheBoard().stream()
                .filter(p -> p.getColor() == movedPiece.getColor())
                .toList();

        System.out.println(movedPiece);
        System.out.println(movedPiece.getColor());

        if (movedPiece instanceof Pawn && movedPiece.getMoveCount() == 1) {
            if (move.getTarget().getColumn() == 3 || move.getTarget().getColumn() == 4) score += 15;
            else score += 5;
        }

        if (movedPiece instanceof Knight && movedPiece.getMoveCount() == 1){
            if (move.getTarget().getColumn() == 2 || move.getTarget().getColumn() == 5) score += 10;
            else score += 5;
        }

        for (Piece piece : pieces) {
            if (pieceIsVulnerable(piece)) {
                if (!pieceIsProtected(piece)) score -= findPieceValue(piece) + 200;
            }
        }

        for (Position position : possibleAttacks) {
            if (chessRules.thereIsAOpponentPiece(position)){
                Piece piece = chessRules.getBoard().piece(position);
                if (pieceIsProtected(piece)) score += findPieceValue(piece) / 10;
                else score += findPieceValue(piece) / 5;
            };
        }

        List<Piece> pawns = pieces.stream().filter(p -> p instanceof Pawn).toList();
        for (Piece pawn : pawns) {
            int col = pawn.getPosition().getColumn();
            int count = (int)pawns.stream().filter(p -> p.getPosition().getColumn() == col && p.getColor() == pawn.getColor()).count();

            if (count > 1) score -= (count - 1) * 10;

            List<Position> positionsDefended = chessRules.possibleAttacks(pawn);
            for (Position position : positionsDefended) {
                if (chessRules.getBoard().positionExists(position) && chessRules.getBoard().thereIsAPiece(position) && chessRules.getBoard().piece(position) instanceof Pawn
                        && chessRules.getBoard().piece(position).getColor() != pawn.getColor()) score += 5;
            }

            if (pawn.getColor() == Color.WHITE) {
                if (pawn.getPosition().getRow() < 4) score += 5;
                if (pawn.getPosition().getRow() < 3) score += 5;
                if (pawn.getPosition().getRow() < 2) score += 5;
                if (pawn.getPosition().getRow() == 0) score += 40;
            } else {
                if (pawn.getPosition().getRow() > 4) score += 5;
                if (pawn.getPosition().getRow() > 5) score += 5;
                if (pawn.getPosition().getRow() > 6) score += 5;
                if (pawn.getPosition().getRow() == 7) score += 40;
            }
         }

        List<Position> mindPositions = List.of(
                new Position(3, 3), new Position(3, 4),
                new Position(4, 3), new Position(4, 4),
                new Position(2, 3), new Position(2, 4),
                new Position(5, 3), new Position(5, 4));

        for (Position position : mindPositions) {
            for (Position attack : possibleAttacks){
                if (position.equals(attack)) score += 5;
            }
        }

         return score;
    }



    private boolean pieceIsProtected(Piece piece) {
        List<Piece> pieces = chessRules.getPiecesOnTheBoard().stream()
                .filter(p -> p.getColor() == piece.getColor())
                .toList();

        for (Piece p : pieces) {
            if (p.possibleMove(piece.getPosition())) return true;
        }

        return false;
    }

    private boolean pieceIsVulnerable(Piece piece) {
        List<Piece> pieces = chessRules.getPiecesOnTheBoard().stream()
                .filter(p -> p.getColor() != piece.getColor())
                .toList();

        for (Piece p : pieces) {
            if (p.possibleMove(piece.getPosition())) return true;
        }

        return false;
    }

    private int findPieceValue(Piece piece) {
        return switch (piece.getType()) {
            case PAWN -> 100;
            case KNIGHT, BISHOP -> 300;
            case ROOK -> 500;
            case QUEEN -> 900;
            default -> 0;
        };
    }
}

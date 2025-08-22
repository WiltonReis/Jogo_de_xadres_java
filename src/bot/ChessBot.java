package bot;

import chessMatch.*;
import chessMatch.ChessPieces.King;
import chessMatch.ChessPieces.Knight;
import chessMatch.ChessPieces.Pawn;

import java.util.*;

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

        if (possibleMoves.size() == 1) return possibleMoves.get(0);

        List<Move> scoredMoves = new ArrayList<>();

        for (Move move : possibleMoves) {

            Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());

            move.setScore(scoreBase() + movedScore(move));
            scoredMoves.add(move);

            chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

        }

        scoredMoves.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        int topMoves = 3;
        if (scoredMoves.size() < topMoves) topMoves = scoredMoves.size();

        List<Move> bestMoves = new ArrayList<>();
        for (int i = 0; i < topMoves; i++) {
            bestMoves.add(scoredMoves.get(i));
        }

        Random random = new Random();
        return bestMoves.get(random.nextInt(bestMoves.size()));
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

        Piece enemyKing = chessRules.getPiecesOnTheBoard().stream().filter(p -> p instanceof King && p.getColor() == chessRules.opponent(movedPiece.getColor())).findFirst().orElse(null);
        List<Position> enemyKingPositions = kingLogicMoves(enemyKing);

        List<Piece> pieces = chessRules.getPiecesOnTheBoard().stream()
                .filter(p -> p.getColor() == movedPiece.getColor())
                .toList();

        List<Position> mindPositions = List.of(
                new Position(3, 3), new Position(3, 4),
                new Position(4, 3), new Position(4, 4),
                new Position(2, 3), new Position(2, 4),
                new Position(5, 3), new Position(5, 4));


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

        Set<Position> mindControl = new HashSet<>();
        Set<Position> kingControl = new HashSet<>();

        for (Piece piece : pieces) {
            if (pieceIsVulnerable(piece)) {
                if (!pieceIsProtected(piece)) score -= findPieceValue(piece) + 200;
                else if (pieceIsProtected(piece)) score -= findPieceValue(piece) / 4;
            }

            List<Position> possibleAttacks = chessRules.possibleAttacks(piece);

            for (Position position : possibleAttacks) {
                if (!chessRules.getBoard().positionExists(position)) break;

                if (chessRules.thereIsAOpponentPiece(position)){
                    Piece opponentPiece = chessRules.getBoard().piece(position);
                    if (pieceIsProtected(opponentPiece)) score += findPieceValue(opponentPiece) / 10;
                    else score += findPieceValue(opponentPiece) / 5;
                }

                for (Position mindPosition : mindPositions) {
                    if (mindPosition.equals(position)) mindControl.add(mindPosition);
                }

                if(chessRules.getBoard().thereIsAPiece(position) && chessRules.getBoard().piece(position).getColor() == movedPiece.getColor()) score += 5;

                for (Position kingMove: enemyKingPositions) {
                    if (kingMove.equals(position)) kingControl.add(kingMove);
                }
            }
        }

        System.out.println(mindControl.size());
        System.out.println(kingControl.size());

        score += mindControl.size() * 5;
        score += kingControl.size() * 10;

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
                if (pawn.getPosition().getRow() == 0) score += 500;
            } else {
                if (pawn.getPosition().getRow() > 4) score += 5;
                if (pawn.getPosition().getRow() > 5) score += 5;
                if (pawn.getPosition().getRow() > 6) score += 5;
                if (pawn.getPosition().getRow() == 7) score += 500;
            }
         }




        if (chessRules.getCheck()){
            if (chessRules.getCheckmate()) score += 5000;

            score += 40 + (8 - possibleMoves(enemyKing).size()) * 10;
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

    private List<Position> kingLogicMoves(Piece king) {
        List<Position> kingMoves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (king.movesLogic()[i][j]) kingMoves.add(new Position(i, j));
            }
        }

        return kingMoves;
    }

    public List<Position> possibleMoves(Piece piece) {
        List<Position> possibleMoves = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessRules.legalMovement(piece.getPosition())[i][j]) possibleMoves.add(new Position(i, j));
            }
        }

        return possibleMoves;
    }
}

package bot;

import chessMatch.*;
import chessMatch.ChessPieces.King;
import chessMatch.ChessPieces.Pawn;

import java.util.*;

public class ChessBot {

    private Color botColor;

    private ChessRules chessRules;

    private final PieceScoreTable pieceScoreTable = new PieceScoreTable();

    public ChessBot(ChessRules chessRules, Color color) {
        this.botColor = color;
        this.chessRules = chessRules;
    }

    public Color getBotColor() {
        return botColor;
    }

    public Move findBestMove() {
        List<Move> possibleMoves = chessRules.possibleMoves(botColor);

        if (possibleMoves.size() == 1) return possibleMoves.get(0);

        List<Move> scoredMoves = new ArrayList<>();
        int depth = 3;

        for (Move move : possibleMoves) {

            boolean oldCheck = chessRules.getCheck();
            boolean oldCheckmate = chessRules.getCheckmate();
            boolean oldStalemate = chessRules.getStalemate();
            boolean oldDraw = chessRules.getDraw();
            Piece oldEnPassant = chessRules.getEnPassant();

            Piece capturedPiece = chessRules.makeMove(move.getSource(), move.getTarget());

            move.setScore(Minimax.minimax(chessRules, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, botColor));
            scoredMoves.add(move);


            chessRules.undoMove(move.getSource(), move.getTarget(), capturedPiece);

            chessRules.setCheck(oldCheck);
            chessRules.setCheckmate(oldCheckmate);
            chessRules.setStalemate(oldStalemate);
            chessRules.setDraw(oldDraw);
            chessRules.setEnPassant(oldEnPassant);

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

    public int evaluateBoard() {
        int score = 0;

        List<Piece> allPieces = chessRules.getPiecesOnTheBoard();

        Piece botKing = getKing(botColor);
        Piece enemyKing = getKing(chessRules.opponent(botColor));

        List<Position> botKingMoves = kingLogicMoves(botKing);
        List<Position> enemyKingMoves = kingLogicMoves(enemyKing);

        List<Piece> whitePawns = new ArrayList<>();
        List<Piece> blackPawns = new ArrayList<>();

        for (Piece piece : allPieces) {
            score += scoreBase(piece);

            if (piece instanceof Pawn) {
                if (piece.getColor() == Color.WHITE) whitePawns.add(piece);
                else blackPawns.add(piece);
            }
        }

        score += evaluateKingSafetyAndAttack(botKingMoves, enemyKingMoves);
        score += evaluateCheck(enemyKing, botColor);

        score += scorePawnStructure(whitePawns, blackPawns);

        return score;
    }

    public int scoreBase(Piece piece) {
        int score = 0;

        int pieceValue = findPieceValue(piece);

        score += piece.getColor() == botColor ? pieceValue : -pieceValue;
        score += evaluatePiece(piece);

        return score;
    }

    private int movedScore(Move move) {

        Piece botKing = getKing(botColor);
        Piece enemyKing = getKing(chessRules.opponent(botColor));

        List<Position> botKingMoves = kingLogicMoves(botKing);
        List<Position> enemyKingMoves = kingLogicMoves(enemyKing);

        int score = 0;

        if (move.getPieceMoved() instanceof King && move.getPieceMoved().getMoveCount() == 0){
            if (move.getTarget().getColumn() == 2 || move.getTarget().getColumn() == 6){
                score += move.getPieceMoved().getColor() == botColor ? + 50 : - 50;
            }
        }

        return score;
    }

    private int evaluatePiece(Piece piece) {
        int score = 0;

        int positionScore = pieceScoreTable.getValue(piece.getType(), piece.getColor(), piece.getPosition());

        score += piece.getColor() == botColor ? positionScore : -positionScore;

        if (pieceIsVulnerable(piece)) {
            score += pieceIsProtected(piece)
                    ? (piece.getColor() == botColor ? -findPieceValue(piece) / 2 : findPieceValue(piece) / 4)
                    : (piece.getColor() == botColor ? -findPieceValue(piece) - 500 : findPieceValue(piece));
        }

        if (pieceIsProtected(piece)) {
            score += (piece.getColor() == botColor ? 10 : -10);
        }

        return score;
    }

    private int scorePawnStructure(List<Piece> whitePawns, List<Piece> blackPawns) {
        int score = 0;

        int whiteScore = evaluatePawnsStructureForColor(whitePawns, blackPawns);
        int blackScore = evaluatePawnsStructureForColor(blackPawns, whitePawns);

        score += botColor == Color.WHITE ? (whiteScore - blackScore) : (blackScore - whiteScore);

        return score;
    }

    private int evaluatePawnsStructureForColor(List<Piece> allyPawns, List<Piece> enemyPawns) {
        int pawnScore = 0;
        final int DOUBLE_PAWN_PENALTY = -10;
        final int ISOLATED_PAWN_PENALTY = -15;
        final int PASSED_PAWN_BONUS = 30;

        Map<Integer, Integer> allyPawnCountByColumn = new HashMap<>();
        Map<Integer, Integer> enemyPawnCountByColumn = new HashMap<>();

        for (Piece pawn : allyPawns) {
            int col = pawn.getPosition().getColumn();
            allyPawnCountByColumn.put(col, allyPawnCountByColumn.getOrDefault(col, 0) + 1);
        }

        for (Piece piece : enemyPawns) {
            int col = piece.getPosition().getColumn();
            enemyPawnCountByColumn.put(col, enemyPawnCountByColumn.getOrDefault(col, 0) + 1);
        }

        for (Piece pawn : allyPawns){
            int col = pawn.getPosition().getColumn();
            int row = pawn.getPosition().getRow();

            Color pawnColor = pawn.getColor();

            if (allyPawnCountByColumn.get(col) > 1) pawnScore += DOUBLE_PAWN_PENALTY;

            boolean hasSupportLeft = allyPawnCountByColumn.containsKey(col - 1);
            boolean hasSupportRight = allyPawnCountByColumn.containsKey(col + 1);

            if (!hasSupportLeft && !hasSupportRight) pawnScore += ISOLATED_PAWN_PENALTY;

            boolean isPassedPawn = true;

            for (int c = col - 1; c<= col; c++){
                if (c < 0 || c > 7) continue;

                for (Piece enemyPawn : enemyPawns){
                    if (enemyPawn.getPosition().getColumn() == c){
                        if (pawnColor == Color.WHITE && enemyPawn.getPosition().getRow() <  row){
                            isPassedPawn = false;
                            break;
                        }
                        if (pawnColor == Color.BLACK && enemyPawn.getPosition().getRow() >  row){
                            isPassedPawn = false;
                            break;
                        }
                    }
                }
            }

            if (isPassedPawn) {
                int passedPawnBonus = 0;

                if (pawnColor == Color.WHITE) {
                    passedPawnBonus = row * 10;
                } else {
                    passedPawnBonus = (8 - row) * 10;
                }

                pawnScore += PASSED_PAWN_BONUS + passedPawnBonus;
            }
        }

        return pawnScore;
    }

    private int evaluateKingSafetyAndAttack(List<Position> botKingMoves, List<Position> enemyKingMoves) {
        int kingScore = 0;

        List<Piece> allPieces = new ArrayList<>(chessRules.getPiecesOnTheBoard());

        for (Piece piece : allPieces) {
            if (piece.getColor() == botColor) {
                for (Position possibleAttack : chessRules.possibleAttacks(piece)){
                    if (enemyKingMoves.contains(possibleAttack)) kingScore += 10;
                }
            } else{
                for (Position possibleAttack : chessRules.possibleAttacks(piece)){
                    if (botKingMoves.contains(possibleAttack)) kingScore -= 10;
                }
            }
        }


        return kingScore;
    }

    private int evaluateCheck(Piece enemyKing, Color botColor) {
        int score = 0;
        if (chessRules.getCheck()) {
            if (pieceIsVulnerable(enemyKing)) {
                score += chessRules.getCheckmate() ? 5000 : 40 + (8 - possibleMoves(enemyKing).size()) * 10;
            } else {
                score -= chessRules.getCheckmate() ? 8000 : 40 + (8 - possibleMoves(enemyKing).size()) * 10;
            }
        }
        return score;
    }

    private Piece getKing(Color color) {
        return chessRules.getPiecesOnTheBoard().stream()
                .filter(p -> p instanceof King && p.getColor() == color)
                .findFirst()
                .orElseThrow();
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

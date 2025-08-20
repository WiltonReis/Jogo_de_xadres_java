package bot;

import chessMatch.*;
import chessMatch.ChessPieces.King;
import chessMatch.ChessPieces.Pawn;
import chessMatch.ChessPieces.TypePiece;

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

        Move bestMove=  possibleMoves.get(0);

        int depth = 3;

        possibleMoves.sort((a, b) -> Integer.compare(Minimax.scoreMove(chessRules, b), Minimax.scoreMove(chessRules, a)));


        if (botColor == Color.WHITE) {

            int maxScore = Integer.MIN_VALUE;
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;

            for (Move move : possibleMoves) {

                GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = Minimax.minimax(chessRules, depth - 1, alpha, beta, false);

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (score > maxScore){
                    maxScore = score;
                    bestMove = move;
                }

                alpha = Math.max(alpha, score);
            }
        } else {

            int minScore = Integer.MAX_VALUE;
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;

            for (Move move : possibleMoves) {

                GameState gameState = chessRules.makeMove(move.getSource(), move.getTarget());

                int score = Minimax.minimax(chessRules, depth - 1, alpha, beta, true);

                chessRules.undoMove(move.getSource(), move.getTarget(), gameState);

                if (score < minScore){
                    minScore = score;
                    bestMove = move;
                }

                beta = Math.min(beta, score);
            }
        }

        return bestMove;
    }

    public int evaluateBoard() {
        int score = 0;

        final int KNIGHT_SCORE_PHASE = 1;
        final int BISHOP_SCORE_PHASE = 1;
        final int ROOK_SCORE_PHASE = 2;
        final int QUEEN_SCORE_PHASE = 4;
        final int TOTAL_SCORE_PHASE = 2 * (KNIGHT_SCORE_PHASE * 2 + BISHOP_SCORE_PHASE * 2 + ROOK_SCORE_PHASE * 2 + QUEEN_SCORE_PHASE);

        List<Piece> allPieces = new ArrayList<>(chessRules.getPiecesOnTheBoard());

        Piece whiteKing = getKing(Color.WHITE);
        Piece blackKing = getKing(Color.BLACK);

        List<Position> whiteKingMoves = kingLogicMoves(whiteKing);
        List<Position> blackKingMoves = kingLogicMoves(blackKing);

        List<Piece> whitePawns = new ArrayList<>();
        List<Piece> blackPawns = new ArrayList<>();

        Set<Position> possibleAttacksWhite = new HashSet<>();
        Set<Position> possibleAttacksBlack = new HashSet<>();

        int currentScorePhase = 0;

        for (Piece piece : allPieces) {
            if(piece.getColor() == Color.WHITE) possibleAttacksWhite.addAll(chessRules.possibleAttacks(piece));
            else possibleAttacksBlack.addAll(chessRules.possibleAttacks(piece));

            if (piece instanceof Pawn) {
                if (piece.getColor() == Color.WHITE) whitePawns.add(piece);
                else blackPawns.add(piece);
            }

            currentScorePhase += switch (piece.getType()) {
                case KNIGHT -> KNIGHT_SCORE_PHASE;
                case BISHOP -> BISHOP_SCORE_PHASE;
                case ROOK -> ROOK_SCORE_PHASE;
                case QUEEN -> QUEEN_SCORE_PHASE;
                default -> 0;
            };
        }

        double gamePhase = Math.min(1.0, (double) currentScorePhase / TOTAL_SCORE_PHASE);

        for (Piece piece : allPieces){
            score += scoreBase(piece, possibleAttacksWhite, possibleAttacksBlack, gamePhase);
        }

        score += evaluateKingSafetyAndAttack(whiteKingMoves, blackKingMoves);
        score += evaluateCheck();

        score += scorePawnStructure(whitePawns, blackPawns);

        return score;
    }

    public int scoreBase(Piece piece, Set<Position> possibleAttacksWhite, Set<Position> possibleAttacksBlack, double gamePhase) {
        int score = 0;

        int pieceValue = findPieceValue(piece);

        score += piece.getColor() == Color.WHITE ? pieceValue : -pieceValue;
        score += evaluatePiece(piece, possibleAttacksWhite, possibleAttacksBlack, gamePhase);

        return score;
    }

    private int evaluatePiece(Piece piece, Set<Position> possibleAttacksWhite, Set<Position> possibleAttacksBlack, double gamePhase) {
        int score = 0;

        score = pieceScoreTable.getValue(piece.getType(), piece.getColor(), piece.getPosition(),gamePhase);

        boolean isVulnerable;
        boolean isProtected;

        if (piece.getColor() == Color.WHITE){
            isVulnerable = possibleAttacksBlack.contains(piece.getPosition());
            isProtected = possibleAttacksWhite.contains(piece.getPosition());
        } else {
            isVulnerable = possibleAttacksWhite.contains(piece.getPosition());
            isProtected = possibleAttacksBlack.contains(piece.getPosition());
        }


        if (isVulnerable) {
            score += isProtected
                    ? (piece.getColor() == botColor ? -findPieceValue(piece) / 2 : findPieceValue(piece) / 4)
                    : (piece.getColor() == botColor ? -findPieceValue(piece) - 300 : findPieceValue(piece));
        }

        if (isProtected) {
            score += 10;
        }

        return piece.getColor() == Color.WHITE ? score : -score;
    }

    private int scorePawnStructure(List<Piece> whitePawns, List<Piece> blackPawns) {

        int whiteScore = evaluatePawnsStructureForColor(whitePawns, blackPawns);
        int blackScore = evaluatePawnsStructureForColor(blackPawns, whitePawns);


        return  whiteScore - blackScore;
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

            for (int c = col - 1; c<= col + 1; c++){
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

    private int evaluateKingSafetyAndAttack(List<Position> whiteKingMoves, List<Position> blackKingMoves) {
        int kingScore = 0;

        List<Piece> allPieces = new ArrayList<>(chessRules.getPiecesOnTheBoard());

        for (Piece piece : allPieces) {
            if (piece.getColor() == Color.WHITE) {
                for (Position possibleAttack : chessRules.possibleAttacks(piece)){
                    if (blackKingMoves.contains(possibleAttack)) kingScore += 10;
                }
            } else{
                for (Position possibleAttack : chessRules.possibleAttacks(piece)){
                    if (whiteKingMoves.contains(possibleAttack)) kingScore -= 10;
                }
            }
        }


        return kingScore;
    }

    private int evaluateCheck() {
        if (!chessRules.getCheck()) {
            return 0;
        }

        int score = 0;

        Piece opponentKing = getKing(chessRules.opponent(chessRules.getTurn()));

        score += chessRules.getCheckmate() ? 9999 : 50 + (8 - chessRules.possibleMovesForPiece(opponentKing).size()) * 10;

        return chessRules.getTurn() == Color.WHITE ?  -score :  score;
    }

    private Piece getKing(Color color) {
        return chessRules.getPiecesOnTheBoard().stream()
                .filter(p -> p instanceof King && p.getColor() == color)
                .findFirst()
                .orElseThrow();
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
}

import bot.ChessBot;
import engine.*;
import engine.ChessPieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChessBotTest {

    ChessRules chessRules;
    Board board;
    ChessBot bot;

    @BeforeEach
    void setUp() {
        chessRules = new ChessRules();
        board = chessRules.getBoard();
        bot = chessRules.getBot();
        clearBoard();

    }

    private void clearBoard() {
        chessRules.getPiecesOnTheBoard().clear();
        chessRules.getCapturedPieces().clear();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.thereIsAPiece(new Position(i, j))) board.removePiece(new Position(i, j));
            }
        }

        chessRules.setEnPassant(null);
        chessRules.setCheck(false);
        chessRules.setCheckmate(false);
        chessRules.setStalemate(false);
        chessRules.setDraw(false);
    }

    private Piece placePiece(TypePiece type, Color color, int row, int column){
        Piece newPiece;
        Position position = new Position(row, column);

        switch (type){
            case PAWN -> newPiece = new Pawn(color, board, chessRules);
            case KNIGHT -> newPiece = new Knight(color, board, chessRules);
            case BISHOP -> newPiece = new Bishop(color, board, chessRules);
            case QUEEN -> newPiece = new Queen(color, board, chessRules);
            case KING -> newPiece = new King(color, board, chessRules);
            case ROOK -> newPiece = new Rook(color, board, chessRules);
            default -> throw new IllegalArgumentException("Invalid type");
        }

        board.placePiece(newPiece, position);
        chessRules.getPiecesOnTheBoard().add(newPiece);

        return newPiece;
    }

    //Evaluation Unit Tests
    @Test
    void testInitialEvaluation(){
        chessRules = new ChessRules();
        bot = chessRules.getBot();

        assertEquals(0, bot.evaluateBoard());
    }

    @Test
    void testEvaluationBoard(){
        placePiece(TypePiece.PAWN, Color.WHITE, 1, 0);
        placePiece(TypePiece.ROOK, Color.BLACK, 0, 0);

        int score = 0;

        for (Piece piece : chessRules.getPiecesOnTheBoard()){
            score += bot.scorePiecesInBoard(piece);
        }

        assertEquals(-400, score);
    }

    @Test
    void testProtectedAndVulnerableScore(){
        placePiece(TypePiece.KING, Color.WHITE, 7, 4);
        placePiece(TypePiece.KING, Color.BLACK, 7, 2);

        Piece whiteRook = placePiece(TypePiece.ROOK, Color.WHITE, 7, 0);
        Piece blackRook = placePiece(TypePiece.ROOK, Color.BLACK, 0, 0);
        Piece blackRook2 = placePiece(TypePiece.ROOK, Color.BLACK, 0, 7);

        Set<Position> possibleAttacksWhite = new HashSet<>();
        Set<Position> possibleAttacksBlack = new HashSet<>();

        possibleAttacksWhite.addAll(chessRules.possibleAttacks(whiteRook));
        possibleAttacksBlack.addAll(chessRules.possibleAttacks(blackRook));
        possibleAttacksBlack.addAll(chessRules.possibleAttacks(blackRook2));

        int vulnerableScore = bot.scoreProtectedAndVulnerablePiece(whiteRook, possibleAttacksWhite, possibleAttacksBlack);
        int vulnerableAndProtectedScore = bot.scoreProtectedAndVulnerablePiece(blackRook, possibleAttacksWhite, possibleAttacksBlack);
        int protectedScore = bot.scoreProtectedAndVulnerablePiece(blackRook2, possibleAttacksWhite, possibleAttacksBlack);

        assertEquals(-800, vulnerableScore);
        assertEquals(125, vulnerableAndProtectedScore);
        assertEquals(-10, protectedScore);
    }

    //Test structure pawns for white
    @Test
    void testDoublePawnPenaltyScoreForWhite(){
        final int DOUBLE_PAWN_PENALTY = -10;

        Piece pawn1 = placePiece(TypePiece.PAWN, Color.WHITE, 6, 0);

        Map<Integer, Integer> allyPawns = new HashMap<>();
        allyPawns.put(pawn1.getPosition().getColumn(), 1);

        int score = bot.doublePawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, DOUBLE_PAWN_PENALTY);

        assertEquals(0, score);

        Piece pawn2 = placePiece(TypePiece.PAWN, Color.WHITE, 5, 0);
        allyPawns.put(pawn2.getPosition().getColumn(), 2);

        score = bot.doublePawnScore(pawn2.getPosition().getColumn(), pawn2.getColor(), allyPawns, DOUBLE_PAWN_PENALTY);

        assertEquals(DOUBLE_PAWN_PENALTY, score);

        score += bot.doublePawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, DOUBLE_PAWN_PENALTY);

        assertEquals(DOUBLE_PAWN_PENALTY * 2, score);
    }

    @Test
    void testIsolatedPawnPenaltyScoreForWhite(){
        final int ISOLATED_PAWN_PENALTY = -15;

        Piece pawn1 = placePiece(TypePiece.PAWN, Color.WHITE, 6, 0);

        Map<Integer, Integer> allyPawns = new HashMap<>();
        allyPawns.put(pawn1.getPosition().getColumn(), 1);

        int score = bot.isolatedPawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, ISOLATED_PAWN_PENALTY);

        assertEquals(ISOLATED_PAWN_PENALTY, score);

        Piece pawn2 = placePiece(TypePiece.PAWN, Color.WHITE, 6, 1);
        allyPawns.put(pawn2.getPosition().getColumn(), 1);

        score = bot.isolatedPawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, ISOLATED_PAWN_PENALTY);

        assertEquals(0, score);
    }

    @Test
    void testPassedPawnScoreForWhite(){
        final int PASSED_PAWN_BONUS = 30;

        Piece whitePawn = placePiece(TypePiece.PAWN, Color.WHITE, 6, 1);

        List<Piece> enemyPawns = new ArrayList<>();

        int colum = whitePawn.getPosition().getColumn();
        int row = whitePawn.getPosition().getRow();

        int passedScore = bot.passedPawnScore(colum, row, whitePawn.getColor(), enemyPawns, PASSED_PAWN_BONUS);

        assertEquals(40, passedScore);

        Piece blackPawn = placePiece(TypePiece.PAWN, Color.BLACK, 2, 1);
        enemyPawns.add(blackPawn);

        int noPassedScore = bot.passedPawnScore(colum, row, whitePawn.getColor(), enemyPawns, PASSED_PAWN_BONUS);

        assertEquals(0, noPassedScore);
    }

    //Test structure pawns for black
    @Test
    void testDoublePawnPenaltyScoreForBlack(){
        final int DOUBLE_PAWN_PENALTY = -10;

        Piece pawn1 = placePiece(TypePiece.PAWN, Color.BLACK, 1, 0);

        Map<Integer, Integer> allyPawns = new HashMap<>();
        allyPawns.put(pawn1.getPosition().getColumn(), 1);

        int score = bot.doublePawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, DOUBLE_PAWN_PENALTY);

        assertEquals(0, score);

        Piece pawn2 = placePiece(TypePiece.PAWN, Color.BLACK, 2, 0);
        allyPawns.put(pawn2.getPosition().getColumn(), 2);

        score = bot.doublePawnScore(pawn2.getPosition().getColumn(), pawn2.getColor(), allyPawns, DOUBLE_PAWN_PENALTY);

        assertEquals(-DOUBLE_PAWN_PENALTY, score);

        score += bot.doublePawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, DOUBLE_PAWN_PENALTY);

        assertEquals(-DOUBLE_PAWN_PENALTY * 2, score);
    }

    @Test
    void testIsolatedPawnPenaltyScoreForBlack(){
        final int ISOLATED_PAWN_PENALTY = -15;

        Piece pawn1 = placePiece(TypePiece.PAWN, Color.BLACK, 1, 0);

        Map<Integer, Integer> allyPawns = new HashMap<>();
        allyPawns.put(pawn1.getPosition().getColumn(), 1);

        int score = bot.isolatedPawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, ISOLATED_PAWN_PENALTY);

        assertEquals(-ISOLATED_PAWN_PENALTY, score);

        Piece pawn2 = placePiece(TypePiece.PAWN, Color.BLACK, 1, 1);
        allyPawns.put(pawn2.getPosition().getColumn(), 1);

        score = bot.isolatedPawnScore(pawn1.getPosition().getColumn(), pawn1.getColor(), allyPawns, ISOLATED_PAWN_PENALTY);

        assertEquals(0, score);
    }

    @Test
    void testPassedPawnScoreForBlack(){
        final int PASSED_PAWN_BONUS = 30;

        Piece whitePawn = placePiece(TypePiece.PAWN, Color.BLACK, 1, 1);

        List<Piece> enemyPawns = new ArrayList<>();

        int colum = whitePawn.getPosition().getColumn();
        int row = whitePawn.getPosition().getRow();

        int passedScore = bot.passedPawnScore(colum, row, whitePawn.getColor(), enemyPawns, PASSED_PAWN_BONUS);

        assertEquals(-40, passedScore);

        Piece blackPawn = placePiece(TypePiece.PAWN, Color.WHITE, 5, 1);
        enemyPawns.add(blackPawn);

        int noPassedScore = bot.passedPawnScore(colum, row, whitePawn.getColor(), enemyPawns, PASSED_PAWN_BONUS);

        assertEquals(0, noPassedScore);
    }

    //test structure pawns total

    @Test
    void testEvaluatePawnStructureIntegration(){
        List<Piece> whitePawns = List.of(
                placePiece(TypePiece.PAWN, Color.WHITE, 6, 0),
                placePiece(TypePiece.PAWN, Color.WHITE, 5, 1)
        );

        List<Piece> blackPawns = List.of(
                placePiece(TypePiece.PAWN, Color.BLACK, 2, 0)
        );

        int score = bot.scorePawnStructure(whitePawns, blackPawns);

        assertTrue(score > 0); // whites are better
    }

    @Test
    void testEvaluateKingAttack(){
        Piece whiteKing = placePiece(TypePiece.KING, Color.WHITE, 7, 4);
        Piece blackKing = placePiece(TypePiece.KING, Color.BLACK, 0, 4);

        List<Position> whiteKingMoves = chessRules.possibleMovesForPiece(whiteKing);
        List<Position> blackKingMoves = chessRules.possibleMovesForPiece(blackKing);

        placePiece(TypePiece.ROOK, Color.WHITE, 7, 5);

        int score = bot.evaluateKingAttack(whiteKingMoves, blackKingMoves);

        assertTrue(score > 0); // whites are better

        placePiece(TypePiece.ROOK, Color.BLACK, 6, 0);

        score = bot.evaluateKingAttack(whiteKingMoves, blackKingMoves);

        assertTrue(score < 0); // blacks are better
    }

    @Test
    void testCheckAndCheckmateScore(){
        Piece whiteKing = placePiece(TypePiece.KING, Color.WHITE, 7, 4);

        placePiece(TypePiece.ROOK, Color.BLACK, 0, 4);
        chessRules.setCheck(true);

        int score = bot.evaluateCheck(whiteKing);

        assertEquals(-90, score);

        placePiece(TypePiece.ROOK, Color.BLACK, 6, 0);

        score = bot.evaluateCheck(whiteKing);

        assertEquals(-110, score);

        placePiece(TypePiece.QUEEN, Color.BLACK, 7, 0);
        chessRules.setCheckmate(true);

        score = bot.evaluateCheck(whiteKing);

        assertTrue(score <= -9999);
    }
}

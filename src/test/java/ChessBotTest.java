import bot.ChessBot;
import engine.*;
import engine.ChessPieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test

}

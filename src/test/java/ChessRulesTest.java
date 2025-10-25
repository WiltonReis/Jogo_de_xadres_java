import engine.*;
import engine.ChessPieces.*;
import engine.ChessPieces.TypePiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the ChessRules class.
public class ChessRulesTest {

    ChessRules chessRules;
    Board board;

    @BeforeEach
    void setUp() {
        chessRules = new ChessRules();
        board = chessRules.getBoard();
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

    private  Piece placePiece(TypePiece type,Color color, int row, int column){
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


    @Test
    void testMakeMove(){

        Position sourcePosition = new Position(6, 0);
        Position targetPosition = new Position(4, 0);

        Piece pawn = placePiece(TypePiece.PAWN, Color.WHITE, 6, 0);

        GameState beforeMove = chessRules.makeMove(sourcePosition, targetPosition);

        Piece capturedPiece = beforeMove.capturedPiece();

        assertNull(chessRules.getBoard().piece(sourcePosition));
        assertEquals(pawn, chessRules.getBoard().piece(targetPosition));
        assertNull(capturedPiece);
        assertEquals(Color.BLACK, chessRules.getTurn());
        assertEquals(1, pawn.getMoveCount());
    }

    @Test
    void testUndoMove(){

        Position sourcePosition = new Position(6, 0);
        Position targetPosition = new Position(5, 1);

        Piece pawn = placePiece(TypePiece.PAWN, Color.WHITE, 6, 0);
        Piece enemyPawn = placePiece(TypePiece.PAWN, Color.BLACK, 5, 1);

        GameState beforeMove = chessRules.makeMove(sourcePosition, targetPosition);

        Piece capturedPiece = beforeMove.capturedPiece();

        chessRules.setCheck(true);
        chessRules.setCheckmate(true);
        chessRules.setStalemate(true);
        chessRules.setDraw(true);
        chessRules.setEnPassant(new Pawn(Color.WHITE, chessRules.getBoard(), chessRules));

        chessRules.undoMove(sourcePosition, targetPosition, beforeMove);

        assertEquals(pawn, chessRules.getBoard().piece(sourcePosition));
        assertEquals(enemyPawn, chessRules.getBoard().piece(targetPosition));
        assertEquals(Color.WHITE, chessRules.getTurn());
        assertFalse(chessRules.getCheck());
        assertFalse(chessRules.getCheckmate());
        assertFalse(chessRules.getStalemate());
        assertFalse(chessRules.getDraw());
        assertNull(chessRules.getEnPassant());
        assertEquals(0, pawn.getMoveCount());

    }

    @Test
    void testCastling(){
        Piece rook = placePiece(TypePiece.ROOK, Color.WHITE, 7, 7);
        Piece king = placePiece(TypePiece.KING, Color.WHITE, 7, 4);

        Position sourceKingPosition = new Position(7, 4);
        Position targetKingPosition = new Position(7, 6);
        Position sourceRookPosition = new Position(7, 7);
        Position targetRookPosition = new Position(7, 5);

        assertEquals(0, king.getMoveCount());
        assertEquals(0, rook.getMoveCount());

        GameState beforeCastling = chessRules.makeMove(sourceKingPosition, targetKingPosition);

        assertEquals(rook, board.piece(targetRookPosition));
        assertNull(board.piece(sourceRookPosition));
        assertEquals(king, board.piece(targetKingPosition));
        assertEquals(1, king.getMoveCount());
        assertEquals(1, rook.getMoveCount());

        chessRules.undoMove(sourceKingPosition, targetKingPosition, beforeCastling);

        assertEquals(rook, board.piece(sourceRookPosition));
        assertNull(board.piece(targetRookPosition));
        assertEquals(king, board.piece(sourceKingPosition));
        assertEquals(0, king.getMoveCount());
        assertEquals(0, rook.getMoveCount());
    }

    @Test
    void testCastlingBig(){
        Piece rook = placePiece(TypePiece.ROOK, Color.WHITE, 7, 0);
        Piece king = placePiece(TypePiece.KING, Color.WHITE, 7, 4);

        Position sourceKingPosition = new Position(7, 4);
        Position targetKingPosition = new Position(7, 2);
        Position sourceRookPosition = new Position(7, 0);
        Position targetRookPosition = new Position(7, 3);

        assertEquals(0, king.getMoveCount());
        assertEquals(0, rook.getMoveCount());

        GameState beforeCastling = chessRules.makeMove(sourceKingPosition, targetKingPosition);

        assertEquals(rook, board.piece(targetRookPosition));
        assertNull(board.piece(sourceRookPosition));
        assertEquals(king, board.piece(targetKingPosition));
        assertEquals(1, king.getMoveCount());
        assertEquals(1, rook.getMoveCount());

        chessRules.undoMove(sourceKingPosition, targetKingPosition, beforeCastling);

        assertEquals(rook, board.piece(sourceRookPosition));
        assertNull(board.piece(targetRookPosition));
        assertEquals(king, board.piece(sourceKingPosition));
        assertEquals(0, king.getMoveCount());
        assertEquals(0, rook.getMoveCount());

    }

    @Test
    void testEnPassant(){

        List<Piece> piecesInBoard = chessRules.getPiecesOnTheBoard();
        List<Piece> capturedPieces = chessRules.getCapturedPieces();

        Piece pawn = placePiece(TypePiece.PAWN, Color.WHITE, 3, 2);
        Piece enemyPawn = placePiece(TypePiece.PAWN, Color.BLACK, 1, 1);

        Position sourceEnemyPawnPosition = new Position(1, 1);
        Position targetEnemyPawnPosition = new Position(3, 1);

        Position sourcePawnPosition = new Position(3, 2);
        Position enPassantPosition = new Position(2, 1);

        chessRules.makeMove(sourceEnemyPawnPosition, targetEnemyPawnPosition);

        assertEquals(enemyPawn, chessRules.getEnPassant());
        assertTrue(pawn.possibleMove(enPassantPosition));

        GameState beforeMove = chessRules.makeMove(sourcePawnPosition, enPassantPosition);

        assertNull(board.piece(targetEnemyPawnPosition));
        assertFalse(piecesInBoard.contains(enemyPawn));
        assertTrue(capturedPieces.contains(enemyPawn));

        chessRules.undoMove(sourcePawnPosition, enPassantPosition, beforeMove);

        assertEquals(enemyPawn, board.piece(targetEnemyPawnPosition));
        assertTrue(piecesInBoard.contains(enemyPawn));
        assertFalse(capturedPieces.contains(enemyPawn));
    }

    @Test
    void testPromotion(){
        Piece pawnPromotion = placePiece(TypePiece.PAWN, Color.WHITE, 1, 0);

        Position sourcePawnPosition = new Position(1, 0);
        Position targetPawnPosition = new Position(0, 0);

        GameState beforeMove = chessRules.makeMove(sourcePawnPosition, targetPawnPosition);
        chessRules.replacePromotedPiece("Queen");

        Piece queenPromotion = board.piece(targetPawnPosition);

        assertEquals(Queen.class, queenPromotion.getClass());
        assertEquals(queenPromotion.getPosition(), targetPawnPosition);
        assertNull(board.piece(sourcePawnPosition));
        assertEquals(pawnPromotion.getColor(), queenPromotion.getColor());
        assertTrue(chessRules.getPiecesOnTheBoard().contains(queenPromotion));
        assertFalse(chessRules.getCapturedPieces().contains(pawnPromotion));

        chessRules.undoMove(sourcePawnPosition, targetPawnPosition, beforeMove);

        Piece pawn = board.piece(sourcePawnPosition);

        assertEquals(Pawn.class, pawn.getClass());
        assertNull(board.piece(targetPawnPosition));
        assertEquals(sourcePawnPosition, pawn.getPosition());
        assertEquals(pawnPromotion.getColor(), pawn.getColor());
        assertTrue(chessRules.getPiecesOnTheBoard().contains(pawn));
        assertFalse(chessRules.getCapturedPieces().contains(queenPromotion));
    }

    @Test
    void testCheck(){
        placePiece(TypePiece.KING, Color.BLACK, 0, 4);
        placePiece(TypePiece.ROOK, Color.BLACK, 0, 0);
        placePiece(TypePiece.KING, Color.WHITE, 7, 3);
        placePiece(TypePiece.ROOK, Color.WHITE, 7, 7);

        assertFalse(chessRules.getCheck());


        Position sourceRookPosition = new Position(7, 7);
        Position targetRookPosition = new Position(7, 4);

        chessRules.performMove(sourceRookPosition, targetRookPosition);


        assertTrue(chessRules.getCheck());
    }

    @Test
    void testCheckmate(){
        placePiece(TypePiece.KING, Color.BLACK, 0, 4);
        placePiece(TypePiece.KING, Color.WHITE, 7, 3);
        placePiece(TypePiece.ROOK, Color.WHITE, 1, 0);
        placePiece(TypePiece.ROOK, Color.WHITE, 7, 7);

        assertFalse(chessRules.getCheckmate());

        Position sourceRookPosition = new Position(7, 7);
        Position targetRookPosition = new Position(0, 7);

        chessRules.performMove(sourceRookPosition, targetRookPosition);

        assertTrue(chessRules.getCheckmate());
    }

    @Test
    void testStalemate(){
        placePiece(TypePiece.KING, Color.BLACK, 0, 0);
        placePiece(TypePiece.KING, Color.WHITE, 7, 4);
        placePiece(TypePiece.ROOK, Color.WHITE, 7, 1);
        placePiece(TypePiece.ROOK, Color.WHITE, 7, 7);

        assertFalse(chessRules.getStalemate());

        Position sourceRookPosition = new Position(7, 7);
        Position targetRookPosition = new Position(1, 7);

        chessRules.performMove(sourceRookPosition, targetRookPosition);

        assertTrue(chessRules.getStalemate());

    }

    @Test
    void testInsufficientMaterial(){
        placePiece(TypePiece.KING, Color.BLACK, 0, 0);
        placePiece(TypePiece.KING, Color.WHITE, 7, 4);

        assertTrue(chessRules.testInsufficientMaterial());

    }
}

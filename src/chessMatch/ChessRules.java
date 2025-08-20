package chessMatch;

import chessMatch.ChessPieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessRules {

    private Board board;
    private Color turn;

    private boolean check;
    private boolean checkmate;
    private Piece enPassant;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessRules() {
        board = new Board();
        turn = Color.WHITE;
        initialSetup();
    }

    public Board getBoard() {
        return board;
    }

    public Color getTurn() {
        return turn;
    }

    public boolean getCheck() {
        return check;
    }

    public Piece getEnPassant() {
        return enPassant;
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public boolean getCheckmate() {
        return checkmate;
    }

    public Piece performMove(Position source, Position target) {
        Position sourcePosition = validateSource(source);
        Position targetPosition = validateTarget(target, sourcePosition);
        Piece movedPiece = board.piece(sourcePosition);
        Piece capturedPiece = makeMove(sourcePosition, targetPosition);


        if (testCheck(turn)){
            check = false;
            undoMove(sourcePosition, targetPosition, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        if (testCheck(opponent(turn))){
            if (testCheckmate(opponent(turn))){
                checkmate = true;
                System.out.println("Checkmate!");
                return null;
            }
            check = true;
            System.out.println("Check!");
        }

        //#specialmove en passant
        if(movedPiece instanceof Pawn && targetPosition.getRow() == sourcePosition.getRow() - 2 || targetPosition.getRow() == sourcePosition.getRow() + 2) {
            enPassant = movedPiece;
            System.out.println(movedPiece);
        }
        else enPassant = null;

        changeTurn();
        return capturedPiece;
    }

    protected Piece makeMove(Position sourcePosition, Position targetPosition) {
        Piece capturedPiece = null;
        Piece piece = board.removePiece(sourcePosition);
        if (board.thereIsAPiece(targetPosition)) capturedPiece = board.removePiece(targetPosition);


        //#specialmove castling
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2){
            Position castlingPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Piece castlingRook = board.removePiece(castlingPosition);
            board.placePiece(castlingRook, new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1));
            castlingRook.incrementMoveCount();
        }
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2){
            Position castlingPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Piece castlingRook = board.removePiece(castlingPosition);
            board.placePiece(castlingRook, new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1));
            castlingRook.incrementMoveCount();
        }

        //#specialmove en passant
        if (piece instanceof Pawn) {
            if(sourcePosition.getColumn() != targetPosition.getColumn() && capturedPiece == null) {
                Position enPassantPosition;
                if (piece.getColor() == Color.WHITE) enPassantPosition = new Position(targetPosition.getRow() + 1, targetPosition.getColumn());
                else enPassantPosition = new Position(targetPosition.getRow() - 1, targetPosition.getColumn());
                capturedPiece = board.removePiece(enPassantPosition);
            }
        }

        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        board.placePiece(piece, targetPosition);

        piece.incrementMoveCount();
        return capturedPiece;
    }

    protected void undoMove(Position sourcePosition, Position targetPosition, Piece capturedPiece) {
        Piece piece = board.removePiece(targetPosition);
        board.placePiece(piece, sourcePosition);
        piece.decrementMoveCount();

        if (capturedPiece != null){
            board.placePiece(capturedPiece, targetPosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        //#specialmove castling
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2){
            Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
            Piece rook = board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.decrementMoveCount();
        }
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2){
            Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
            Piece rook = board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.decrementMoveCount();
        }

        //#specialmove en passant
        if (piece instanceof Pawn) {
            if(sourcePosition.getColumn() != targetPosition.getColumn() && capturedPiece == enPassant) {
                Position enPassantPosition;
                Piece pawn = board.removePiece(targetPosition);
                if (piece.getColor() == Color.WHITE) enPassantPosition = new Position(3, targetPosition.getColumn());
                else enPassantPosition = new Position(4, targetPosition.getColumn());
                board.placePiece(capturedPiece, enPassantPosition);
            }
        }


    }

    private Position validateSource(Position source) {
        if (!board.thereIsAPiece(source)) throw new ChessException("There is no piece on source position");
        return source;
    }

    private Position validateTarget(Position target, Position source) {
        if (!board.piece(source).possibleMove(target)) throw new ChessException("The chosen piece can't move to target position");
        return target;
    }

    public boolean testCheck(Color color) {
        Position kingPosition = king(color).getPosition();
        return piecesOnTheBoard.stream()
                .filter(piece -> piece.getColor() == opponent(color))
                .anyMatch(piece -> piece.possibleAttack(kingPosition));
    }

    public boolean testCheckmate(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream()
                .filter(piece -> piece.getColor() == color)
                .toList();

        for (Piece piece : pieces) {
            boolean[][] possibleMoves = piece.movesLogic();

            for (int i = 0; i < possibleMoves.length; i++) {
                for (int j = 0; j < possibleMoves[i].length; j++) {
                    if (possibleMoves[i][j]) {
                        Position source = piece.getPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) return false;
                    }
                }
            }
        }
        return true;
    }

    private void changeTurn() {
        turn = opponent(turn);
    }

    public boolean isYourPiece(Piece piece) {
        return piece.getColor() == turn;
    }

    public Color opponent(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public Piece king(Color color) {
        return piecesOnTheBoard.stream()
                .filter(piece -> piece instanceof King)
                .filter(piece -> piece.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    public void initialSetup() {
        piecesOnTheBoard.addAll(List.of(
                board.placeNewPiece(new Rook(Color.WHITE, board, this), new Position(7, 0)),
                board.placeNewPiece(new Knight(Color.WHITE, board, this), new Position(7, 1)),
                board.placeNewPiece(new Bishop(Color.WHITE, board, this), new Position(7, 2)),
                board.placeNewPiece(new Queen(Color.WHITE, board, this), new Position(7, 3)),
                board.placeNewPiece(new King(Color.WHITE, board, this), new Position(7, 4)),
                board.placeNewPiece(new Bishop(Color.WHITE, board, this), new Position(7, 5)),
                board.placeNewPiece(new Knight(Color.WHITE, board, this), new Position(7, 6)),
                board.placeNewPiece(new Rook(Color.WHITE, board, this), new Position(7, 7)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 0)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 1)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 2)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 3)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 4)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 5)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 6)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 7)),

                board.placeNewPiece(new Rook(Color.BLACK, board, this), new Position(0, 0)),
                board.placeNewPiece(new Knight(Color.BLACK, board, this), new Position(0, 1)),
                board.placeNewPiece(new Bishop(Color.BLACK, board, this), new Position(0, 2)),
                board.placeNewPiece(new Queen(Color.BLACK, board, this), new Position(0, 3)),
                board.placeNewPiece(new King(Color.BLACK, board, this), new Position(0, 4)),
                board.placeNewPiece(new Bishop(Color.BLACK, board, this), new Position(0, 5)),
                board.placeNewPiece(new Knight(Color.BLACK, board, this), new Position(0, 6)),
                board.placeNewPiece(new Rook(Color.BLACK, board, this), new Position(0, 7)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 0)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 1)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 2)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 3)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 4)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 5)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 6)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 7))
        ));
    }
}
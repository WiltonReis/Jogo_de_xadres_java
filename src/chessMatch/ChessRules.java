package chessMatch;

import chessMatch.ChessPieces.King;
import chessMatch.ChessPieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessRules {

    private Board board;
    private Color turn;

    private boolean check;

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

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public Piece performMove(Position source, Position target) {
        Position sourcePosition = validateSource(source);
        Position targetPosition = validateTarget(target, sourcePosition);
        Piece capturedPiece = makeMove(sourcePosition, targetPosition);

        if (testCheck(turn)){
            //testCheckmate();
            check = false;
            undoMove(sourcePosition, targetPosition, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }


        if (testCheck(opponent(turn))){
            check = true;
            System.out.println("Check!");
        }

        changeTurn();
        return capturedPiece;
    }

    protected Piece makeMove(Position sourcePosition, Position targetPosition) {
        Piece capturedPiece = null;
        Piece piece = board.removePiece(sourcePosition);
        if (board.thereIsAPiece(targetPosition)) capturedPiece = board.removePiece(targetPosition);
        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        board.placePiece(piece, targetPosition);
        return capturedPiece;
    }

    protected void undoMove(Position sourcePosition, Position targetPosition, Piece capturedPiece) {
        Piece piece = board.removePiece(targetPosition);
        if (capturedPiece != null){
            board.placePiece(capturedPiece, targetPosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
        board.placePiece(piece, sourcePosition);
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
            board.placeNewPiece(new Rook(Color.WHITE, board, this), new Position(7, 7)),
            board.placeNewPiece(new Rook(Color.WHITE, board, this), new Position(7, 0)),
            board.placeNewPiece(new Rook(Color.BLACK, board, this), new Position(0, 0)),
            board.placeNewPiece(new Rook(Color.BLACK, board, this), new Position(0, 7)),
            board.placeNewPiece(new King(Color.WHITE, board, this), new Position(7, 4)),
            board.placeNewPiece(new King(Color.BLACK, board, this), new Position(0, 4))
        ));
    }
}
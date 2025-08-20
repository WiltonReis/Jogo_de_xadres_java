package chessMatch;

public class ChessRules {

    private Board board;

    public ChessRules() {
        board = new Board();
    }

    public Board getBoard() {
        return board;
    }

    public Piece performMove(Position source, Position target) {
        Position sourcePosition = validateSource(source);
        Position targetPosition = validateTarget(target, sourcePosition);
        return makeMove(sourcePosition, targetPosition);
    }

    private Piece makeMove(Position sourcePosition, Position targetPosition) {
        Piece capturedPiece = null;
        Piece piece = board.removePiece(sourcePosition);
        if (board.thereIsAPiece(targetPosition)) capturedPiece = board.removePiece(targetPosition);
        board.placePiece(piece, targetPosition);
        return capturedPiece;
    }

    private Position validateSource(Position source) {
        if (!board.thereIsAPiece(source)) return null;
        return source;
    }

    public Position validateTarget(Position target, Position source) {
        if (!board.piece(source).possibleMove(target)) return null;
        return target;
    }
}

package chessMatch;

public class ChessRules {

    private Board board;
    private Color turn;

    public ChessRules() {
        board = new Board();
        turn = Color.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public Color getTurn() {
        return turn;
    }

    public Piece performMove(Position source, Position target) {
        Position sourcePosition = validateSource(source);
        Position targetPosition = validateTarget(target, sourcePosition);
        Piece capturedPiece = makeMove(sourcePosition, targetPosition);
        changeTurn();
        return capturedPiece;
    }

    private Piece makeMove(Position sourcePosition, Position targetPosition) {
        Piece capturedPiece = null;
        Piece piece = board.removePiece(sourcePosition);
        if (board.thereIsAPiece(targetPosition)) capturedPiece = board.removePiece(targetPosition);
        board.placePiece(piece, targetPosition);
        return capturedPiece;
    }

    private Position validateSource(Position source) {
        if (!board.thereIsAPiece(source)) throw new ChessException("There is no piece on source position");
        return source;
    }

    private Position validateTarget(Position target, Position source) {
        if (!board.piece(source).possibleMove(target)) throw new ChessException("The chosen piece can't move to target position");
        return target;
    }

    public boolean isYourPiece(Piece piece) {
        return piece.getColor() == turn;
    }

    private void changeTurn() {
        turn = opponent(turn);
    }

    public Color opponent(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}

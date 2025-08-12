package chessMatch;

public abstract class Piece {

    protected Color color;
    protected Position position;
    protected Board board;
    protected ChessRules chessRules;
    protected int moveCount;

    public Piece(Color color, Board board, ChessRules chessRules) {
        this.chessRules = chessRules;
        this.color = color;
        this.board = board;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public void decrementMoveCount() {
        moveCount--;
    }

    public Board getBoard() {
        return board;
    }

    public abstract boolean[][] movesLogic();


    public boolean possibleMove(Position position) {
        return movesLogic()[position.getRow()][position.getColumn()];
    }

    public boolean isThereOpponentPiece(Position position) {
        if(!board.thereIsAPiece(position)) return false;
        return board.piece(position).getColor() != color;
    }
}
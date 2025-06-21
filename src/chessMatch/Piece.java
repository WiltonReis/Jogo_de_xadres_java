package chessMatch;

public abstract class Piece {

    private Color color;
    private Position position;
    private Board board;

    public Piece(Color color, Position position, Board board) {
        this.color = color;
        this.board = board;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public abstract boolean[][] possibleMove();
}

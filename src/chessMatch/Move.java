package chessMatch;

public class Move {

    private Position source;
    private Position target;
    private Piece pieceMoved;
    private int score;

    public Move(Position source, Position target, Piece pieceMoved) {
        this.source = source;
        this.target = target;
        this.pieceMoved = pieceMoved;
    }

    public Position getSource() {
        return source;
    }

    public Position getTarget() {
        return target;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

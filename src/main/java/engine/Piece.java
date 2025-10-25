package engine;

import engine.ChessPieces.TypePiece;

public abstract class Piece {

    protected Color color;
    protected TypePiece type;
    protected Position position;
    protected Board board;
    protected ChessRules chessRules;
    protected int moveCount;

    public Piece(Color color, Board board, ChessRules chessRules, TypePiece type) {
        this.type = type;
        this.chessRules = chessRules;
        this.color = color;
        this.board = board;
    }

    public Color getColor() {
        return color;
    }

    public TypePiece getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public int getMoveCount() {
        return moveCount;
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
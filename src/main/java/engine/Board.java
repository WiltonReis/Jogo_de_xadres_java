package engine;

public class Board {

    private Piece[][] pieces;

    public Board() {
        pieces = new Piece[8][8];
    }

    public Piece piece(int row, int column) {
        return pieces[row][column];
    }

    public Piece piece(Position position) {
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.setPosition(position);
    }

    public Piece placeNewPiece(Piece piece, Position position) {
        if (thereIsAPiece(position)) throw new ChessException("There is already a piece on this position");
        placePiece(piece, position);
        return piece;
    }

    public Piece removePiece(Position position) {
        if (!positionExists(position)) throw new ChessException("Position not on the board");
        if (piece(position) == null) throw new ChessException("There is no piece on this position");
        Piece aux = piece(position);
        aux.setPosition(null);
        pieces[position.getRow()][position.getColumn()] = null;
        return aux;
    }

    public boolean positionExists(Position position) {
        return position.getRow() < 8 && position.getRow() >= 0 && position.getColumn() < 8 && position.getColumn() >= 0;
    }

    public boolean thereIsAPiece(Position position) {
        return piece(position) != null;
    }

}
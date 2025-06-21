package chessMatch;

import chessMatch.ChessPieces.Rook;

public class Board {

    private Piece[][] pieces;

    public Board() {
        pieces = new Piece[8][8];
        initialSetup();
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

    public Piece removePiece(Position position) {
        Piece aux = piece(position);
        aux.position = null;
        pieces[position.getRow()][position.getColumn()] = null;
        return aux;
    }

    public boolean positionExists(Position position) {
        return position.getRow() < 8 && position.getRow() >= 0 && position.getColumn() < 8 && position.getColumn() >= 0;
    }

    public boolean thereIsAPiece(Position position) {
        return piece(position) != null;
    }

    public void initialSetup() {
        placePiece(new Rook(Color.WHITE, this), new Position(7, 7));
        placePiece(new Rook(Color.WHITE, this), new Position(7, 0));
        placePiece(new Rook(Color.BLACK, this), new Position(0, 0));
        placePiece(new Rook(Color.BLACK, this), new Position(0, 7));
    }
}

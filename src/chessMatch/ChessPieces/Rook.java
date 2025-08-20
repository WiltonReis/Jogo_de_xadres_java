package chessMatch.ChessPieces;

import chessMatch.Board;
import chessMatch.Color;
import chessMatch.Piece;
import chessMatch.Position;

public class Rook extends Piece {

    public Rook(Color color, Board board) {
        super(color, board);
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[8][8];

        Position p = new Position(0, 0);

        // above
        p.setValues(position.getRow() - 1, position.getColumn());
        while (board.positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setRow(p.getRow() - 1);
        }
        if (board.positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }

        // left
        p.setValues(position.getRow(), position.getColumn() - 1);
        while (board.positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setColumn(p.getColumn() - 1);
        }
        if (board.positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }

        // right
        p.setValues(position.getRow(), position.getColumn() + 1);
        while (board.positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setColumn(p.getColumn() + 1);
        }
        if (board.positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }

        // below
        p.setValues(position.getRow() + 1, position.getColumn());
        while (board.positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setRow(p.getRow() + 1);
        }
        if (board.positionExists(p) && isThereOpponentPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
        }

        return moves;
    }

    @Override
    public String toString() {
        if (color == Color.WHITE) {
            return "whiteRook";
        } else {
            return "blackRook";
        }
    }
}
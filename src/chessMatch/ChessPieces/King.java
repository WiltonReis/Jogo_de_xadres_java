package chessMatch.ChessPieces;

import chessMatch.*;

public class King extends Piece {

    public King(Color color, Board board, ChessRules chessRules) {
        super(color, board, chessRules);
    }

    @Override
    public boolean[][] movesLogic() {
        boolean[][] moves = new boolean[8][8];

        Position p = new Position(0, 0);

        // above
        p.setValues(position.getRow() - 1, position.getColumn());
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // left
        p.setValues(position.getRow(), position.getColumn() - 1);
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // right
        p.setValues(position.getRow(), position.getColumn() + 1);
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // below
        p.setValues(position.getRow() + 1, position.getColumn());
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // nw
        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // ne
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // sw
        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // se
        p.setValues(position.getRow() + 1, position.getColumn() + 1);
        if (board.positionExists(p) && !board.thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        if (board.positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        return moves;
    }

    @Override
    public String toString() {
        return Color.WHITE == color ? "whiteKing" : "blackKing";
    }
}

package engine.ChessPieces;

import engine.*;

public class Knight extends Piece {

    public Knight(Color color, Board board, ChessRules chessRules) {
        super(color, board, chessRules, TypePiece.KNIGHT);
    }

    @Override
    public boolean[][] movesLogic() {
        boolean[][] moves = new boolean[8][8];

        Position p = new Position(0, 0);

        // nw
        p.setValues(position.getRow() - 2, position.getColumn() - 1);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // ne
        p.setValues(position.getRow() - 2, position.getColumn() + 1);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // se
        p.setValues(position.getRow() + 2, position.getColumn() + 1);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // sw
        p.setValues(position.getRow() + 2, position.getColumn() - 1);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // nw
        p.setValues(position.getRow() - 1, position.getColumn() - 2);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // ne
        p.setValues(position.getRow() - 1, position.getColumn() + 2);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // se
        p.setValues(position.getRow() + 1, position.getColumn() + 2);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        // sw
        p.setValues(position.getRow() + 1, position.getColumn() - 2);
        if(getBoard().positionExists(p) && (!board.thereIsAPiece(p) || isThereOpponentPiece(p))) moves[p.getRow()][p.getColumn()] = true;

        return moves;
    }

    @Override
    public String toString() {
        return Color.WHITE == color ? "whiteKnight" : "blackKnight";
    }
}

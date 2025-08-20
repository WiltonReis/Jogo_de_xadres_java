package chessMatch.ChessPieces;

import chessMatch.*;

public class Bishop extends Piece {

    public Bishop(Color color, Board board, ChessRules chessRules) {
        super(color, board, chessRules);
    }

    @Override
    public boolean[][] movesLogic() {
        boolean[][] moves = new boolean[8][8];

        Position p = new Position(0, 0);

        // nw
        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        while(getBoard().positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() - 1, p.getColumn() - 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // ne
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        while(getBoard().positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() - 1, p.getColumn() + 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // se
        p.setValues(position.getRow() + 1, position.getColumn() + 1);
        while(getBoard().positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() + 1, p.getColumn() + 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        // sw
        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        while(getBoard().positionExists(p) && !board.thereIsAPiece(p)) {
            moves[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() + 1, p.getColumn() - 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;

        return moves;
    }

    @Override
    public String toString() {
        return Color.WHITE == color ? "whiteBishop" : "blackBishop";
    }
}
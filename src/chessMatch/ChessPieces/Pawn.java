package chessMatch.ChessPieces;

import chessMatch.*;

public class Pawn extends Piece {

    public Pawn(Color color, Board board, ChessRules chessRules) {
        super(color, board, chessRules);
    }

    @Override
    public boolean[][] movesLogic() {
        boolean[][] moves = new boolean[8][8];

        Position p = new Position(0, 0);

        if(color == Color.WHITE) {
            p.setValues(position.getRow() - 1, position.getColumn());
            if(moveCount == 0){
                Position p2 = new Position(position.getRow() - 2, position.getColumn());
                if (getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && !getBoard().thereIsAPiece(p)) moves[p2.getRow()][p2.getColumn()] = true;
            }
            if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
            p.setValues(position.getRow() - 1, position.getColumn() - 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;
            p.setValues(position.getRow() - 1, position.getColumn() + 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        } else {
            p.setValues(position.getRow() + 1, position.getColumn());
            if(moveCount == 0){
                Position p2 = new Position(position.getRow() + 2, position.getColumn());
                if (getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && !getBoard().thereIsAPiece(p)) moves[p2.getRow()][p2.getColumn()] = true;
            }
            if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) moves[p.getRow()][p.getColumn()] = true;
            p.setValues(position.getRow() + 1, position.getColumn() - 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;
            p.setValues(position.getRow() + 1, position.getColumn() + 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)) moves[p.getRow()][p.getColumn()] = true;
        }

        return moves;
    }

    @Override
    public boolean possibleAttack(Position position) {
        if(movesLogic()[position.getRow()][position.getColumn()] && position.getColumn() != this.position.getColumn()) return true;
        return false;
    }

    @Override
    public String toString() {
        return Color.WHITE == color ? "whitePawn" : "blackPawn";
    }
}
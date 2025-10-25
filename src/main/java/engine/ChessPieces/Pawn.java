package engine.ChessPieces;

import engine.*;

public class Pawn extends Piece {

    public Pawn(Color color, Board board, ChessRules chessRules) {
        super(color, board, chessRules, TypePiece.PAWN);
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

            //#specialMove en passant white
            if (position.getRow() == 3) {
                Piece piece;

                //left
                Position left = new Position(position.getRow(), position.getColumn() - 1);
                if (board.positionExists(left) && board.thereIsAPiece(left)) {
                    piece = board.piece(left);
                    if (piece instanceof Pawn && piece.getColor() == Color.BLACK && chessRules.getEnPassant() == piece) {
                        moves[position.getRow() - 1][position.getColumn() - 1] = true;
                    }
                }

                //right
                Position right = new Position(position.getRow(), position.getColumn() + 1);
                if (board.positionExists(right) && board.thereIsAPiece(right)) {
                    piece = board.piece(right);
                    if (piece instanceof Pawn && piece.getColor() == Color.BLACK && chessRules.getEnPassant() == piece) {
                        moves[position.getRow() - 1][position.getColumn() + 1] = true;
                    }
                }
            }


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

            //#specialMove en passant
            if (position.getRow() == 4) {
                Piece piece;
                //left
                Position left = new Position(position.getRow(), position.getColumn() - 1);
                if (board.positionExists(left) && board.thereIsAPiece(left)) {
                    piece = board.piece(left);
                    if (piece instanceof Pawn && piece.getColor() == Color.WHITE && chessRules.getEnPassant() == piece) {
                        moves[position.getRow() + 1][position.getColumn() - 1] = true;
                    }
                }

                        //right
                Position right = new Position(position.getRow(), position.getColumn() + 1);
                if (board.positionExists(right) && board.thereIsAPiece(right)) {
                    piece = board.piece(right);
                    if (piece instanceof Pawn && piece.getColor() == Color.WHITE && chessRules.getEnPassant() == piece) {
                        moves[position.getRow() + 1][position.getColumn() + 1] = true;
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return Color.WHITE == color ? "whitePawn" : "blackPawn";
    }
}
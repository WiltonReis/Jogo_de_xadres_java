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

        // #specialmove castling
        if (moveCount == 0 && !chessRules.getCheck()) {
            Position rookPosition = new Position(0,0);
            Piece rook = null;

            // right castling
            rookPosition.setValues(position.getRow(), position.getColumn() + 3);
            if (board.thereIsAPiece(rookPosition)){
                rook = board.piece(rookPosition);
                if (rook instanceof Rook && ((Rook) rook).fitForCastling()) {
                    Position p1 = new Position(position.getRow(), position.getColumn() + 1);
                    Position p2 = new Position(position.getRow(), position.getColumn() + 2);
                    if (!board.thereIsAPiece(p1) && !board.thereIsAPiece(p2)) {
                        moves[position.getRow()][position.getColumn() + 2] = true;
                    }
                }
            }

            // left castling
            rookPosition.setValues(position.getRow(), position.getColumn() - 4);
            if (board.thereIsAPiece(rookPosition)){
                rook = board.piece(rookPosition);
                if (rook instanceof Rook && ((Rook) rook).fitForCastling()) {
                    Position p1 = new Position(position.getRow(), position.getColumn() - 1);
                    Position p2 = new Position(position.getRow(), position.getColumn() - 2);
                    Position p3 = new Position(position.getRow(), position.getColumn() - 3);
                    if (!board.thereIsAPiece(p1) && !board.thereIsAPiece(p2) && !board.thereIsAPiece(p3)) {
                        moves[position.getRow()][position.getColumn() - 2] = true;
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return Color.WHITE == color ? "whiteKing" : "blackKing";
    }
}

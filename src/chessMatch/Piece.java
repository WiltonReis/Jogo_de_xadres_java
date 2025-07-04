package chessMatch;

public abstract class Piece {

    protected Color color;
    protected Position position;
    protected Board board;
    protected ChessRules chessRules;
    protected int moveCount;

    public Piece(Color color, Board board, ChessRules chessRules) {
        this.chessRules = chessRules;
        this.color = color;
        this.board = board;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
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

    public boolean[][] possibleMoves() {
        boolean[][] moves = movesLogic();
        Piece capturedPiece;

        Position sourcePosition = position;

        for (int i = 0; i < moves.length; i++) {
            for (int j = 0; j < moves[i].length; j++) {
                if (moves[i][j]) {
                     Position targetPosition = new Position(i, j);

                    capturedPiece = chessRules.makeMove(sourcePosition, targetPosition);

                    if (chessRules.testCheck(color)) {
                        moves[i][j] = false;
                    }
                    chessRules.undoMove(sourcePosition, targetPosition, capturedPiece);
                }
            }
        }
        return moves;
    }

    public boolean possibleMove(Position position) {
        return possibleMoves()[position.getRow()][position.getColumn()];
    }

    public boolean possibleAttack(Position position) {
        return movesLogic()[position.getRow()][position.getColumn()];
    }

    public boolean isThereAnyPossibleMove() {
        boolean[][] moves = possibleMoves();
        for (int i = 0; i < moves.length; i++) {
            for (int j = 0; j < moves[i].length; j++) {
                if (moves[i][j]) return true;
            }
        }
        return false;
    }

    public boolean isThereOpponentPiece(Position position) {
        if(!board.thereIsAPiece(position)) return false;
        return board.piece(position).getColor() != color;
    }
}
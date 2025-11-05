package engine;

import bot.ChessBot;
import engine.ChessPieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessRules {

    private Board board;
    private Color turn;

    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private boolean draw;
    private boolean IfEnPassantMove;
    private Piece enPassant;
    private Piece promoted;
    private ChessBot bot;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessRules() {
        board = new Board();
        turn = Color.WHITE;
        initialSetup();
        bot = new ChessBot(this, Color.BLACK);
    }

    public Board getBoard() {
        return board;
    }

    public Color getTurn() {
        return turn;
    }

    public boolean getCheck() {
        return check;
    }

    public Piece getEnPassant() {
        return enPassant;
    }

    public Piece getPromoted() {
        return promoted;
    }

    public ChessBot getBot() {
        return bot;
    }

    public List<Piece> getPiecesOnTheBoard() {
        return piecesOnTheBoard;
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public boolean getCheckmate() {
        return checkmate;
    }

    public boolean getStalemate() {
        return stalemate;
    }

    public boolean getDraw() {
        return draw;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public void setStalemate(boolean stalemate) {
        this.stalemate = stalemate;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public void setEnPassant(Piece enPassant) {
        this.enPassant = enPassant;
    }

    public Piece performMove(Position source, Position target) {
        Position sourcePosition = validateSource(source);
        Position targetPosition = validateTarget(target, sourcePosition);
        GameState gameState = makeMove(sourcePosition, targetPosition);

        Piece capturedPiece = gameState.capturedPiece();


        if (testCheck(gameState.turn())){
            undoMove(sourcePosition, targetPosition, gameState);
            throw new ChessException("You can't put yourself in check");
        }

        if (hasAnyLegalMove(turn)){
            check = testCheck(turn);
        } else {
            checkmate = testCheck(turn);
            stalemate = !checkmate;
        }

        return capturedPiece;
    }

    public void botMove(){
        Move move = bot.findBestMove();
        performMove(move.getSource(), move.getTarget());
    }

    public GameState makeMove(Position sourcePosition, Position targetPosition) {
        GameState previousState = new GameState(turn, check, checkmate, stalemate, draw, enPassant, null, false);

        Piece movedPiece = board.removePiece(sourcePosition);

        Piece capturedPiece = null;

        boolean ifEnPassantMove = false;

        if (board.thereIsAPiece(targetPosition))
            capturedPiece = board.removePiece(targetPosition);


        board.placePiece(movedPiece, targetPosition);

        movedPiece.incrementMoveCount();

        //#specialmove pawn
        if (movedPiece instanceof Pawn) {

            //#specialmove en passant capture
            if (capturedPiece == null && targetPosition.getColumn() != sourcePosition.getColumn()) {
                Position enPassantPosition = new Position(sourcePosition.getRow(), targetPosition.getColumn());
                Piece enPassantPiece = board.piece(enPassantPosition);
                ifEnPassantMove = true;
                if (enPassantPiece == enPassant) capturedPiece = board.removePiece(enPassantPosition);
            }

            //#specialmove pawn promotion
            if (targetPosition.getRow() == 0 || targetPosition.getRow() == 7) {
                promoted = movedPiece;
            } else promoted = null;
        } else promoted = null;

        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }


            //#specialmove castling
        if (movedPiece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2){
            Position castlingPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Piece castlingRook = board.removePiece(castlingPosition);
            board.placePiece(castlingRook, new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1));
            castlingRook.incrementMoveCount();
        }
        if (movedPiece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2){
            Position castlingPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Piece castlingRook = board.removePiece(castlingPosition);
            board.placePiece(castlingRook, new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1));
            castlingRook.incrementMoveCount();
        }

        turn = opponent(turn);

        check = false;
        checkmate = false;
        stalemate = false;
        draw = false;
        enPassant = null;

        //#specialmove en passant set
        if (movedPiece instanceof Pawn) {
            if (targetPosition.getRow() == sourcePosition.getRow() - 2 || targetPosition.getRow() == sourcePosition.getRow() + 2) {
                enPassant = movedPiece;
            }
        }

        return new GameState(previousState.turn(), previousState.check(), previousState.checkmate(), previousState.stalemate(),
                previousState.draw(), previousState.enPassant(), capturedPiece, ifEnPassantMove);
    }

    public void undoMove(Position sourcePosition, Position targetPosition, GameState previousState) {
        Piece piece = board.removePiece(targetPosition);
        board.placePiece(piece, sourcePosition);
        piece.decrementMoveCount();

        Piece capturedPiece = previousState.capturedPiece();

        if (capturedPiece != null){

            //#specialmove en passant
            Position capturedPiecePosition;
            if (piece instanceof Pawn && sourcePosition.getColumn() != targetPosition.getColumn() && capturedPiece == previousState.enPassant() && previousState.ifEnPassant()) {
                capturedPiecePosition = new Position(sourcePosition.getRow(), targetPosition.getColumn());
            } else {
                capturedPiecePosition = targetPosition;
            }

            board.placePiece(capturedPiece, capturedPiecePosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        //#specialmove promotion
        if (promoted != null) {
            Piece promotedPiece = board.removePiece(sourcePosition);
            board.placePiece(promoted, sourcePosition);
            piecesOnTheBoard.add(promoted);
            piecesOnTheBoard.remove(promotedPiece);
            promoted = null;
        }

        //#specialmove castling
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2){
            Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
            Piece rook = board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.decrementMoveCount();
        }
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2){
            Position sourceRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Position targetRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
            Piece rook = board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.decrementMoveCount();
        }

        changeTurn();

        check = previousState.check();
        checkmate = previousState.checkmate();
        stalemate = previousState.stalemate();
        draw = previousState.draw();
        enPassant = previousState.enPassant();
    }

    public void replacePromotedPiece(String pieceType) {
        if (promoted == null) throw new ChessException("There is no piece to be promoted");

        Position position = promoted.getPosition();
        Color color = promoted.getColor();

        board.removePiece(position);
        piecesOnTheBoard.remove(promoted);

        Piece newPiece = switch (pieceType) {
            case "Rook" -> new Rook(color, board, this);
            case "Knight" -> new Knight(color, board, this);
            case "Bishop" -> new Bishop(color, board, this);
            default -> new Queen(color, board, this);
        };

        board.placePiece(newPiece, position);
        piecesOnTheBoard.add(newPiece);
    }

    private Position validateSource(Position source) {
        if (!board.thereIsAPiece(source)) throw new ChessException("There is no piece on source position");
        return source;
    }

    private Position validateTarget(Position target, Position source) {
        if (!board.piece(source).possibleMove(target)) throw new ChessException("The chosen piece can't move to target position");
        return target;
    }

    public boolean testCheck(Color color) {
        Position kingPosition = king(color).getPosition();
        return piecesOnTheBoard.stream()
                .filter(piece -> piece.getColor() == opponent(color))
                .anyMatch(piece -> piece.possibleMove(kingPosition));
    }

    public boolean testInsufficientMaterial() {
        if (piecesOnTheBoard.size() == 2) return true;

        List<Bishop> bishopsList = piecesOnTheBoard.stream().filter(piece -> piece instanceof Bishop).map(piece -> (Bishop)piece).toList();

        int knights = (int)piecesOnTheBoard.stream().filter(piece -> piece instanceof Knight).count();;

        int bishops = bishopsList.size();

        if (piecesOnTheBoard.size() == 3) if (bishops == 1 || knights == 1) return true;


        if (piecesOnTheBoard.size() == 4){
            if (knights == 2) return true;

            if (bishops == 2){
                if (bishopsList.stream().allMatch(b -> (b.getPosition().getColumn() + b.getPosition().getRow()) % 2 ==
                        (bishopsList.get(0).getPosition().getColumn() + bishopsList.get(0).getPosition().getRow()) % 2)) return true;
            }

        }
        return false;
    }

    public boolean[][] legalMovement(Position sourcePosition){
        Piece piece = board.piece(sourcePosition);
        boolean[][] legalMoves = new boolean[8][8];

        boolean[][] movesLogic = piece.movesLogic();

        for (int i = 0; i < movesLogic.length; i++) {
            for (int j = 0; j < movesLogic[i].length; j++) {
                if (movesLogic[i][j]) {
                    Position targetPosition = new Position(i, j);

                    GameState gameState = makeMove(sourcePosition, targetPosition);
                    boolean testCheck = testCheck(piece.getColor());
                    undoMove(sourcePosition, targetPosition, gameState);
                    if (!testCheck) legalMoves[i][j] = true;
                }
                }
            }
        return legalMoves;
    }

    public boolean possibleMove(Position sourcePosition, Position targetPosition) {
        return legalMovement(sourcePosition)[targetPosition.getRow()][targetPosition.getColumn()];
    }

    private boolean hasAnyLegalMove(Color color) {
         List<Piece> pieces = piecesOnTheBoard.stream()
                .filter(piece -> piece.getColor() == color)
                .toList();

        for (Piece piece : pieces) {
            boolean[][] legalMoves = legalMovement(piece.getPosition());
            for (int i = 0; i < legalMoves.length; i++) {
                for (int j = 0; j < legalMoves[i].length; j++) {
                    if (legalMoves[i][j]) return true;
                }
            }
        }
        return false;
    }

    public List<Move> possibleMoves(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream().filter(piece -> piece.getColor() == color).toList();
        List<Move> moves = new ArrayList<>();

        boolean[][] pieceMoves;

        for (Piece piece : pieces) {
            pieceMoves = piece.movesLogic();

            for (int i = 0; i < pieceMoves.length; i++) {
                for (int j = 0; j < pieceMoves[i].length; j++) {
                    if (pieceMoves[i][j]) {

                        Position sourcePosition = new Position(piece.getPosition().getRow(), piece.getPosition().getColumn());
                        Position targetPosition = new Position(i, j);

                        GameState gameState = makeMove(sourcePosition, targetPosition);
                        boolean testCheck = testCheck(piece.getColor());
                        undoMove(sourcePosition, targetPosition, gameState);

                        if (testCheck) continue;

                        Piece capturedPiece = gameState.capturedPiece();

                        moves.add(new Move(sourcePosition, targetPosition, piece, capturedPiece));
                    }
                }
            }
        }
        return moves;
    }

    public List<Position> possibleMovesForPiece(Piece piece) {
        List<Position> possibleMoves = new ArrayList<>();

        boolean[][] pieceMoves = piece.movesLogic();

        for (int i = 0; i < pieceMoves.length; i++) {
            for (int j = 0; j < pieceMoves[i].length; j++) {
                if (pieceMoves[i][j]) possibleMoves.add(new Position(i, j));
            }
        }
        return possibleMoves;
    }

    public List<Position> possibleAttacks(Piece piece) {
        List<Position> attacksPosition = new ArrayList<>();

        if (piece instanceof Pawn) {
            if (piece.getColor() == Color.WHITE) {
                attacksPosition.add(new Position(piece.getPosition().getRow() - 1, piece.getPosition().getColumn() - 1));
                attacksPosition.add(new Position(piece.getPosition().getRow() - 1, piece.getPosition().getColumn() + 1));
            } else {
                attacksPosition.add(new Position(piece.getPosition().getRow() + 1, piece.getPosition().getColumn() - 1));
                attacksPosition.add(new Position(piece.getPosition().getRow() + 1, piece.getPosition().getColumn() + 1));
            }
        } else {
            boolean[][] legalMoves = legalMovement(piece.getPosition());
            for (int i = 0; i < legalMoves.length; i++) {
                for (int j = 0; j < legalMoves[i].length; j++) {
                    if (legalMoves[i][j]) attacksPosition.add(new Position(i, j));
                }
            }
        }

        return attacksPosition;
    }

    public boolean thereIsAOpponentPiece(Position position) {
        return piecesOnTheBoard.stream()
                .filter(piece -> piece.getColor() == opponent(turn))
                .anyMatch(piece -> piece.getPosition().equals(position));
    }

    private void changeTurn() {
        turn = opponent(turn);
    }

    public boolean isYourPiece(Piece piece) {
        return piece.getColor() == turn;
    }

    public Color opponent(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public Piece king(Color color) {
        return piecesOnTheBoard.stream()
                .filter(piece -> piece instanceof King)
                .filter(piece -> piece.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    public void initialSetup() {
        piecesOnTheBoard.addAll(List.of(
                board.placeNewPiece(new Rook(Color.WHITE, board, this), new Position(7, 0)),
                board.placeNewPiece(new Knight(Color.WHITE, board, this), new Position(7, 1)),
                board.placeNewPiece(new Bishop(Color.WHITE, board, this), new Position(7, 2)),
                board.placeNewPiece(new Queen(Color.WHITE, board, this), new Position(7, 3)),
                board.placeNewPiece(new King(Color.WHITE, board, this), new Position(7, 4)),
                board.placeNewPiece(new Bishop(Color.WHITE, board, this), new Position(7, 5)),
                board.placeNewPiece(new Knight(Color.WHITE, board, this), new Position(7, 6)),
                board.placeNewPiece(new Rook(Color.WHITE, board, this), new Position(7, 7)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 0)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 1)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 2)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 3)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 4)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 5)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 6)),
                board.placeNewPiece(new Pawn(Color.WHITE, board, this), new Position(6, 7)),

                board.placeNewPiece(new Rook(Color.BLACK, board, this), new Position(0, 0)),
                board.placeNewPiece(new Knight(Color.BLACK, board, this), new Position(0, 1)),
                board.placeNewPiece(new Bishop(Color.BLACK, board, this), new Position(0, 2)),
                board.placeNewPiece(new Queen(Color.BLACK, board, this), new Position(0, 3)),
                board.placeNewPiece(new King(Color.BLACK, board, this), new Position(0, 4)),
                board.placeNewPiece(new Bishop(Color.BLACK, board, this), new Position(0, 5)),
                board.placeNewPiece(new Knight(Color.BLACK, board, this), new Position(0, 6)),
                board.placeNewPiece(new Rook(Color.BLACK, board, this), new Position(0, 7)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 0)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 1)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 2)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 3)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 4)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 5)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 6)),
                board.placeNewPiece(new Pawn(Color.BLACK, board, this), new Position(1, 7))
        ));
    }
}
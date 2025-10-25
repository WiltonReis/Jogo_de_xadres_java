package engine;

public record GameState (
    Color turn,
    boolean check,
    boolean checkmate,
    boolean stalemate,
    boolean draw,
    Piece enPassant,
    Piece capturedPiece,
    boolean ifEnPassant
){}

package gui;

import chessMatch.ChessRules;
import chessMatch.Piece;
import chessMatch.Position;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    @FXML
    private GridPane chessBoard;

    @FXML
    private HBox capturedPiecesWhite;

    @FXML
    private HBox capturedPiecesBlack;

    private static final int BOARD_SIZE = 8;
    private static final double CELL_SIZE = 60.0;

    private ChessRules chessRules;
    private Position sourcePosition;
    private StackPane selectedCellPane;
    private ImageView selectedImageView;
    private ImageView draggedPieceImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chessRules = new ChessRules();
        drawBoard();
        updateBoard();
        onCellClick();
    }

    private void drawBoard() {

        Color lightColor = Color.web("#D4E1C6");
        Color darkColor = Color.web("#8BB0BF");


        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);

                if ((row + col) % 2 == 0)  cell.setFill(lightColor);
                else cell.setFill(darkColor);

                StackPane stackPane = new StackPane(cell);
                stackPane.setId("cell-" + row + "-" + col);
                stackPane.getStyleClass().add("chess-cell");

                chessBoard.add(stackPane, col, row);
            }
        }
    }

    public void onCellClick() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane cellPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);
                if (cellPane != null) {
                    final int finalRow = row;
                    final int finalCol = col;

                    cellPane.setOnMouseClicked(event -> handlerCellClick(finalRow, finalCol));
                    dragAndDrop(cellPane, finalRow, finalCol);
                }
            }
        }
    }

    private void tryPerformMove(Position targetPosition) {
        if (sourcePosition == null) {
            clearSelection();
            return;
        }

        try {
            chessRules.performMove(sourcePosition, targetPosition);
            updateBoard();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            clearSelection();
        }
    }

    private void handlerCellClick(int row, int col) {
        Position positonClicked = new Position(row, col);
        StackPane cellPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);

        clearHighlightPossibleMoves();

        if (sourcePosition == null)  {
            if (chessRules.getBoard().thereIsAPiece(positonClicked) && chessRules.isYourPiece(chessRules.getBoard().piece(positonClicked))) {
                sourcePosition = positonClicked;
                selectedCellPane = cellPane;

                selectedImageView = findImageViewInCell(cellPane);
                if (selectedImageView != null) {
                    selectedImageView.getStyleClass().add("selected-cell");
                }

                highlightPossibleMoves(sourcePosition);
            } else System.out.println("There is no piece in this position");
        } else {
            if (sourcePosition.equals(positonClicked)) {
                clearSelection();
                return;
            }

            if (chessRules.getBoard().thereIsAPiece(positonClicked) && chessRules.isYourPiece(chessRules.getBoard().piece(positonClicked))) {
                clearSelection();
                sourcePosition = positonClicked;
                selectedCellPane = cellPane;

                selectedImageView = findImageViewInCell(cellPane);
                if (selectedImageView != null) {
                    selectedImageView.getStyleClass().add("selected-cell");
                }

                highlightPossibleMoves(sourcePosition);
                return;
            }

            tryPerformMove(positonClicked);
        }
    }

    private void dragAndDrop(StackPane cellPane, int finalRow, int finalCol) {
        cellPane.setOnDragDetected(event -> {
            Position currentPosition = new Position(finalRow, finalCol);
            if (chessRules.getBoard().thereIsAPiece(currentPosition) && chessRules.isYourPiece(chessRules.getBoard().piece(currentPosition))) {
                sourcePosition = currentPosition;
                selectedCellPane = cellPane;

                for (Node node : selectedCellPane.getChildren()) {
                    if (node instanceof ImageView) {
                        draggedPieceImageView = (ImageView) node;
                        break;
                    }
                }

                if (draggedPieceImageView != null) {
                    selectedCellPane.getChildren().remove(draggedPieceImageView);

                    Dragboard db = cellPane.startDragAndDrop(TransferMode.MOVE);



                    SnapshotParameters snapshotParams = new SnapshotParameters();
                    snapshotParams.setFill(Color.TRANSPARENT);

                    WritableImage snapshot = new WritableImage(
                            (int)draggedPieceImageView.getFitWidth(),
                            (int)draggedPieceImageView.getFitHeight()
                    );
                    draggedPieceImageView.snapshot(snapshotParams, snapshot);
                    db.setDragView(snapshot);

                    db.setDragViewOffsetX(draggedPieceImageView.getFitWidth() / 2);
                    db.setDragViewOffsetY(draggedPieceImageView.getFitHeight() / 2);

                    ClipboardContent content = new ClipboardContent();
                    content.putString(finalRow + "," + finalCol);
                    db.setContent(content);

                    highlightPossibleMoves(sourcePosition);

                } else {
                    clearSelection();
                }
                event.consume();
            } else {
                System.out.println("There is no piece in this position");
            }
        });

        cellPane.setOnDragOver(event -> {
            if (event.getGestureSource() != cellPane && event.getDragboard().hasString()) {
                Position targetCandidate = new Position(finalRow, finalCol);
                if (sourcePosition != null && chessRules.getBoard().piece(sourcePosition) != null &&
                        chessRules.getBoard().piece(sourcePosition).possibleMove(targetCandidate)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });

        cellPane.setOnDragExited(event -> {
            event.consume();
        });

        cellPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && sourcePosition != null) {
                Position targetPosition = new Position(finalRow, finalCol);
                tryPerformMove(targetPosition);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        cellPane.setOnDragDone(event -> {
            if (!event.isDropCompleted() && draggedPieceImageView != null && selectedCellPane != null) {
                if (!selectedCellPane.getChildren().contains(draggedPieceImageView)) {
                    selectedCellPane.getChildren().add(draggedPieceImageView);
                }
            }
            clearSelection();
            draggedPieceImageView = null;
            event.consume();
        });
    }

    private ImageView findImageViewInCell(StackPane cellPane) {
        for (Node node : cellPane.getChildren()) {
            if (node instanceof ImageView) {
                return (ImageView) node;
            }
        }
        return null;
    }

    private void clearSelection() {
        if (selectedImageView != null) {
            selectedImageView.getStyleClass().remove("selected-cell");
            selectedImageView = null;
        }
        selectedCellPane = null;
        sourcePosition = null;
        clearHighlightPossibleMoves();
    }

    private void highlightPossibleMoves(Position sourcePosition) {
        boolean[][] possibleMoves = chessRules.getBoard().piece(sourcePosition).possibleMoves();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (possibleMoves[row][col]) {
                    StackPane targetCellPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);
                    if (targetCellPane != null) {
                        Circle circle = new Circle();
                        circle.setRadius(CELL_SIZE * 0.15);
                        circle.setFill(Color.web("#333333", 0.4));
                        targetCellPane.getChildren().add(circle);
                    }
                }
            }
        }
    }

    private void clearHighlightPossibleMoves() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane targetCellPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);
                if (targetCellPane != null) {
                    targetCellPane.getChildren().removeIf(node -> node instanceof Circle);
                }
            }
        }
    }

    private void updateBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane cellPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);
                cellPane.getChildren().removeIf(node -> !(node instanceof Rectangle));
            }
        }
        drawPieces();
        updateCapturedPieces();
    }

    private void drawPieces() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = chessRules.getBoard().piece(row, col);
                if (piece != null) {
                    try {

                        ImageView imageView = createImageViewForPiece(piece);

                        imageView.setFitWidth(CELL_SIZE * 0.9);
                        imageView.setFitHeight(CELL_SIZE * 0.9);

                        StackPane targetPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);

                        if(targetPane != null) {
                            targetPane.getChildren().add(imageView);
                        } else {
                            System.err.println("Cell not found");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateCapturedPieces() {
        capturedPiecesWhite.getChildren().clear();
        capturedPiecesBlack.getChildren().clear();

        for (Piece piece : chessRules.getCapturedPieces()){
            try {
                ImageView imageView = createImageViewForPiece(piece);
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);

                if (piece.getColor() == chessMatch.Color.WHITE) {
                    capturedPiecesWhite.getChildren().add(imageView);
                } else {
                    capturedPiecesBlack.getChildren().add(imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private ImageView createImageViewForPiece(Piece piece) throws Exception {
        String pieceImage = piece.toString() + ".png";
        String imagePath = "/gui/pieces/" + pieceImage;

        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(CELL_SIZE * 0.9);
        imageView.setFitHeight(CELL_SIZE * 0.9);
        imageView.setPreserveRatio(true);

        return imageView;
    }
}
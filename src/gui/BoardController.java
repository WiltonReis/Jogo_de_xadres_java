package gui;

import chessMatch.ChessRules;
import chessMatch.Piece;
import chessMatch.Position;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    @FXML
    private GridPane chessBoard;

    private static final int BOARD_SIZE = 8;
    private static final double CELL_SIZE = 60.0;

    private ChessRules chessRules;
    private Position sourcePosition;
    private StackPane selectedCellPane;
    private Border originCellBorder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chessRules = new ChessRules();
        drawBoard();

        StackPane tempPane = (StackPane) chessBoard.lookup("#cell-0-0");
        if (tempPane != null) {
            originCellBorder = tempPane.getBorder();
        }

        updateBoard();
        onCellClick();
    }

    private void drawBoard() {

        Color lightColor = Color.web("#D4E1C6");
        Color darkColor = Color.web("#8BB0BF");

        Border cellBorder = new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                null,
                new BorderWidths(1.5)
        ));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);

                if ((row + col) % 2 == 0)  cell.setFill(lightColor);
                else cell.setFill(darkColor);

                StackPane stackPane = new StackPane(cell);
                stackPane.setId("cell-" + row + "-" + col);
                stackPane.setBorder(cellBorder);

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
                }
            }
        }
    }

    private void handlerCellClick(int row, int col) {
        Position positonClicked = new Position(row, col);
        StackPane cellPane = (StackPane) chessBoard.lookup("#cell-" + row + "-" + col);

        clearHighlightPossibleMoves();

        if (sourcePosition == null) {
            if (chessRules.getBoard().thereIsAPiece(positonClicked)) {
                sourcePosition = positonClicked;
                selectedCellPane = cellPane;

                selectedCellPane.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(3))));

                highlightPossibleMoves(sourcePosition);
            } else System.out.println("There is no piece in this position");
        } else {
            if (sourcePosition.equals(positonClicked)) {
                clearSelection();
                return;
            }

            try {
                chessRules.performMove(sourcePosition, positonClicked);
                updateBoard();
                clearSelection();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                clearSelection();
            }
        }
    }

    private void clearSelection() {
        if (selectedCellPane != null) {
            selectedCellPane.setBorder(originCellBorder);
            selectedCellPane = null;
        }
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
    }

    private void drawPieces() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = chessRules.getBoard().piece(row, col);
                if (piece != null) {
                    try {
                        String pieceImage = piece.toString() + ".png";
                        String imagePath = "/gui/pieces/" + pieceImage;

                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        ImageView imageView = new ImageView(image);

                        imageView.setFitWidth(CELL_SIZE * 0.9);
                        imageView.setFitHeight(CELL_SIZE * 0.9);
                        imageView.setPreserveRatio(true);

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
}
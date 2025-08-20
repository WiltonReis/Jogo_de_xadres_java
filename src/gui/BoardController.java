package gui;

import chessMatch.Board;
import chessMatch.Piece;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    @FXML
    private GridPane chessBoard;

    private static final int BOARD_SIZE = 8;
    private static final double CELL_SIZE = 60.0;
    private Board board;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        board = new Board();
        drawBoard();
        drawPieces();
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

    private void drawPieces() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board.piece(row, col);
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
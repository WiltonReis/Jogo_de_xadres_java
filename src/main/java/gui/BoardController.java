package gui;

import engine.ChessRules;
import engine.Piece;
import engine.Position;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class BoardController implements Initializable {

    @FXML
    private StackPane mainPane;

    @FXML
    private GridPane chessBoard;

    @FXML
    private Pane themesPane;

    @FXML
    private HBox capturedPiecesWhite;

    @FXML
    private HBox capturedPiecesBlack;

    @FXML
    private VBox endGameView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label playerWinsLabel;

    @FXML
    private Button newGameButton;

    private static final int BOARD_SIZE = 8;
    private static final double CELL_SIZE = 60.0;

    private ChessRules chessRules;
    private Position sourcePosition;
    private StackPane selectedCellPane;
    private ImageView selectedImageView;
    private ImageView draggedPieceImageView;

    public void onNewGameAction() {
        newGame();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        endGameView.setVisible(false);
        endGameView.setManaged(false);

        chessRules = new ChessRules();
        drawBoard();
        try {
            createThemesMenu();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setBoardTheme("wood");
        updateBoard();
        onCellClick();
    }

    private void drawBoard() {
        double borderWidth = 1.5;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.getStyleClass().add((row + col) % 2 == 0 ? "light-cell" : "dark-cell");

                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(borderWidth);

                StackPane stackPane = new StackPane(cell);
                stackPane.setId("cell-" + row + "-" + col);

                chessBoard.add(stackPane, col, row);
            }
        }
    }

    public void setBoardTheme(String theme) {
        chessBoard.getStyleClass().removeIf(s -> s.startsWith("theme-"));
        chessBoard.getStyleClass().add("theme-" + theme);
    }

    public void openThemeMenu() throws IOException {
        themesPane.setVisible(true);
        themesPane.setManaged(true);
        chessBoard.setDisable(true);
    }

    public void closeThemeMenu() {
        themesPane.setVisible(false);
        themesPane.setManaged(false);
        chessBoard.setDisable(false);
    }

    private void createThemesMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/themes/ThemesView.fxml"));
        themesPane = loader.load();

        ThemesViewController themesController = loader.getController();
        themesController.setBoardController(this);

        themesPane.setVisible(false);
        themesPane.setManaged(false);

        mainPane.getChildren().add(themesPane);
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

    private void playerMove(Position targetPosition) {
        tryPerformMove(sourcePosition, targetPosition);
    }

    public void botMove() {
        double delay = ThreadLocalRandom.current().nextDouble(0.05);

        PauseTransition pause = new PauseTransition(Duration.seconds(delay));
        pause.setOnFinished(event -> {
            chessRules.botMove();
            if (chessRules.getPromoted() != null) chessRules.replacePromotedPiece("Queen");
            updateBoard();
            checkEndGame();
        });

        pause.play();
    }

    private void tryPerformMove(Position sourcePosition, Position targetPosition) {
        if (sourcePosition == null) {
            clearSelection();
            return;
        }
        try {
            chessRules.performMove(sourcePosition, targetPosition);
            updateBoard();
            if (chessRules.getPromoted() != null) loadPromotedPieceView(chessRules.getPromoted());
            checkEndGame();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearSelection();
        }

        if (chessRules.getTurn() == chessRules.getBot().getBotColor()) {
            botMove();
        }
    }

    private void checkEndGame() {
        if (chessRules.getCheckmate()){
            System.out.println("Checkmate");
            String KingWinner = chessRules.getTurn() != engine.Color.WHITE ? "whiteKing.png" : "blackKing.png";
            Image kingWins = new Image("/images/pieces/" + KingWinner);
            ImageView imageView = new ImageView(kingWins);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            playerWinsLabel.setGraphic(imageView);
            loadEndGameView("Checkmate", chessRules.getTurn() != engine.Color.WHITE ? "Brancas vencem" : "Pretas vencem");
        }
        if (chessRules.getStalemate()) loadEndGameView("Afogamento", "Empate");
        if (chessRules.getDraw()) loadEndGameView("Material Insuficiente", "Empate");
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

            playerMove(positonClicked);
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
                playerMove(targetPosition);
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

    private void loadPromotedPieceView(Piece pawnPromoted) {

        chessBoard.setDisable(true);

        int row = pawnPromoted.getPosition().getRow();
        int col = pawnPromoted.getPosition().getColumn();

        HBox promotionBox = new HBox(10);
        promotionBox.setPadding(new Insets(10));

        promotionBox.getStyleClass().add("promoted-view");

        String colorPrefix = (pawnPromoted.getColor() == engine.Color.WHITE) ? "white" : "black";
        String[] Pieces = {"Queen", "Rook", "Bishop", "Knight"};

        for (String pieceName : Pieces) {
            ImageView pieceImageView = new ImageView(new Image("/images/pieces/" + colorPrefix + pieceName + ".png"));
            pieceImageView.setFitWidth(40);
            pieceImageView.setFitHeight(40);

            pieceImageView.setOnMouseClicked(event -> {
                chessRules.replacePromotedPiece(pieceName);
                mainPane.getChildren().remove(promotionBox);
                chessBoard.setDisable(false);
                updateBoard();
            });

            promotionBox.getChildren().add(pieceImageView);
        }

        double cellX = (col + 1) * CELL_SIZE;
        double cellY = (row + 1) * CELL_SIZE;
        double boxWidthEstimate = 210;
        double boxHeightEstimate = 60;

        if (col > 3) {
            promotionBox.setTranslateX(cellX - boxWidthEstimate * 1.3 - 120);
            if (pawnPromoted.getColor() == engine.Color.WHITE) {
                promotionBox.setTranslateY(cellY - boxHeightEstimate * 4.5 + 45);
                promotionBox.getStyleClass().add("promotion-pane-wr");
            } else {
                promotionBox.setTranslateY(cellY - boxHeightEstimate * 4.5 - 45);
                promotionBox.getStyleClass().add("promotion-pane-br");
            }
        } else {
            promotionBox.setTranslateX(cellX - boxWidthEstimate * 1.3 + 120);
            if (pawnPromoted.getColor() == engine.Color.WHITE) {
                promotionBox.setTranslateY(cellY - boxHeightEstimate * 4.5 + 60);
                promotionBox.getStyleClass().add("promotion-pane-wl");
            } else {
                promotionBox.setTranslateY(cellY - boxHeightEstimate * 4.5 - 60);
                promotionBox.getStyleClass().add("promotion-pane-bl");
            }
        }


        mainPane.getChildren().add(promotionBox);
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
        boolean[][] possibleMoves = chessRules.legalMovement(sourcePosition);
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

                if (piece.getColor() == engine.Color.WHITE) {
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
        String imagePath = "/images/pieces/" + pieceImage;

        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(CELL_SIZE * 0.9);
        imageView.setFitHeight(CELL_SIZE * 0.9);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    private void loadEndGameView(String message, String playerWins) {
        titleLabel.setText(message);
        playerWinsLabel.setText(playerWins);

        endGameView.setManaged(true);
        endGameView.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(500), endGameView);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        chessBoard.setDisable(true);
    }

    private void newGame() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), endGameView);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(event -> {
            endGameView.setVisible(false);
            endGameView.setManaged(false);
        });
        ft.play();

        chessRules = new ChessRules();
        updateBoard();
        clearSelection();

        chessBoard.setDisable(false);
    }
}
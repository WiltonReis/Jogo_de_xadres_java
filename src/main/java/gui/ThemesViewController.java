package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemesViewController implements Initializable {

    @FXML
    private Button close;

    @FXML
    private Button themePink;

    @FXML
    private Button themeBlue;

    @FXML
    private Button themeWood;

    @FXML
    private Button themeDark;

    @FXML
    private Button add;

    private String theme;

    private BoardController boardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onCloseAction() {
        boardController.closeThemeMenu();
    }

    public void setBoardController(BoardController boardController) {
        this.boardController = boardController;
    }

    public void onThemePinkAction() {
        theme = "pink";
    }

    public void onThemeBlueAction() {
        theme = "blue";
    }

    public void onThemeWoodAction() {
        theme = "wood";
    }

    public void onThemeDarkAction() {
        theme = "dark";
    }

    public void ondAddAction() {
        if (theme == null) return;
        boardController.setBoardTheme(theme);
        boardController.closeThemeMenu();
    }
}

package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/gui/Board.fxml"));
        VBox root = fxmlLoader.load();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.show();
    }

    public static Scene getScene() {
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package pw.spn.lifegame;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameOfLife extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        setUpPrimaryState(primaryStage);
        primaryStage.setScene(buildUI());
        primaryStage.show();
    }

    private void setUpPrimaryState(Stage primaryStage) {
        primaryStage.setTitle("The Game of Life");
        primaryStage.setMaximized(true);
        Image applicationIcon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(applicationIcon);

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F11) {
                if (primaryStage.isFullScreen()) {
                    primaryStage.setFullScreen(false);
                } else {
                    primaryStage.setFullScreen(true);
                }
            }
        });
    }

    private Scene buildUI() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/Game.fxml"));
        BorderPane root = loader.<BorderPane> load();
        Scene scene = new Scene(root, 1280, 1024);
        scene.getStylesheets().add(getClass().getResource("ui/application.css").toExternalForm());
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

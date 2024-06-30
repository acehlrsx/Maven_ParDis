package views;

import javafx.application.Application;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import design.RoundBorder;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));

        RoundBorder.setRoundedWindow(primaryStage, root);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

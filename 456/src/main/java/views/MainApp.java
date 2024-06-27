package views;

import javafx.application.Application;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));

       // Create a rounded rectangle as the stage shape
        Rectangle roundedRect = new Rectangle(1280, 720);
        roundedRect.setArcWidth(20);
        roundedRect.setArcHeight(20);

        // Create a transparent scene with rounded rectangle shape
        Scene scene = new Scene(root, 1280, 720);
        scene.setFill(Color.TRANSPARENT);
        scene.getRoot().setClip(roundedRect);

        // Set the stage properties
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

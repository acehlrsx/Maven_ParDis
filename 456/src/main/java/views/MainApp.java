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
import model.DatabaseHelper;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseHelper.createTable();
        DatabaseHelper.createItineraryTable();
        DatabaseHelper.createDaysTable();
        DatabaseHelper.createPlacesTable();
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));

        RoundBorder.setRoundedWindow(primaryStage, root);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

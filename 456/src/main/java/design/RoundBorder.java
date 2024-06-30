package design;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RoundBorder {

    public static void setRoundedWindow(Stage stage, Parent root) {
        Rectangle roundedRect = new Rectangle(1280, 720);
        roundedRect.setArcWidth(20);  
        roundedRect.setArcHeight(20); 

        Scene scene = new Scene(root, 1280, 720); 
        scene.setFill(Color.TRANSPARENT);
        scene.getRoot().setClip(roundedRect);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
    }
}

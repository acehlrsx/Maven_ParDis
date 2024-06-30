package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class addDayController {

    private List<VBox> dayPanelsContainers = new ArrayList<>();

    @FXML
    private VBox placesContainer;

    private static int dayCount = 0;
    private double initialY = 212;
    private double yIncrement = 200;

    @FXML
    public void AddDayPanel(VBox anchorTest, AnchorPane anchorTest1) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addDayPanel.fxml"));
            VBox dayPanelsContainer = loader.load(); 
            Label dayLabel = (Label) loader.getNamespace().get("dayLabel");

            updateDayLabel(dayLabel);

            System.out.println(dayLabel);
        
            initialY += yIncrement;
            anchorTest.getChildren().add(dayPanelsContainer); 

            double newHeight = initialY + anchorTest.getPrefHeight(); 
            anchorTest1.setPrefHeight(newHeight);

            dayPanelsContainers.add(dayPanelsContainer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private addPlaceController addPlaceControllerInstance;

    public void initialize() {
        addPlaceControllerInstance = new addPlaceController();
    }

    @FXML
    void AddPlace() {
        addPlaceControllerInstance.AddPlace(placesContainer);
    }

    @FXML
    void removeDay(ActionEvent event) {
        Button removeButton = (Button) event.getSource();
        VBox dayPanelContainer = (VBox) removeButton.getParent().getParent();
        VBox anchorPane = (VBox) dayPanelContainer.getParent();
        anchorPane.getChildren().remove(dayPanelContainer);

        dayPanelsContainers.remove(dayPanelContainer);
    }

    private void updateDayLabel(Label dayLabel) {
        dayCount++;
        dayLabel.setText("Day " + dayCount);
    }

    public List<VBox> getDayPanelsContainers() {
        return dayPanelsContainers;
    }
    
}

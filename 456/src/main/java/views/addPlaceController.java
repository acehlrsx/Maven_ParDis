package views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class addPlaceController {
    private List<TextField> timeFields = new ArrayList<>();
    private List<TextField> locFields = new ArrayList<>();
    private List<TextField> placetimeFields = new ArrayList<>();
    private List<TextField> placelocFields = new ArrayList<>();
    private Set<Integer> usedIds = new HashSet<>();
    private Random random = new Random();

     @FXML
    private Button deletePlaceButton;

    @FXML
    private TextField placeDestinationField;

    @FXML
    private TextField placeTimeField;

    @FXML
    void deletePlace(ActionEvent event) {
        HBox placePanel = (HBox) deletePlaceButton.getParent();
        VBox placesContainer = (VBox) placePanel.getParent();

        placesContainer.getChildren().remove(placePanel);
    }
    
    @FXML
    void AddPlace(VBox placesContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlacePanel.fxml"));
            HBox placePanel = loader.load();

            // int newId = generateUniqueRandomId();
            // usedIds.add(newId);

            // TextField timeField = (TextField) placePanel.lookup("#placeTimeField");
            // TextField locField = (TextField) placePanel.lookup("#placeDestinationField");

            // String timeFieldId = "placeTimeField_" + newId;
            // String locFieldId = "placeDestinationField_" + newId;
            // timeField.setId(timeFieldId);
            // locField.setId(locFieldId);

            // timeFields.add(timeField);
            // locFields.add(locField);

            // System.out.println(timeField);
            // System.out.println(timeFields);

            placesContainer.getChildren().add(placePanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int generateUniqueRandomId() {
        int id;
        do {
            id = random.nextInt(1000);
        } while (usedIds.contains(id));
        return id;
    }

    public List<TextField> getTimeFields() {
        return timeFields;
    }

    public List<TextField> getLocFields() {
        return locFields;
    }
}

package views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class addPlaceController {
    private List<TextField> timeFields = new ArrayList<>();
    private List<TextField> locFields = new ArrayList<>();

    @FXML
    private ComboBox<String> placeAMPM;

    @FXML
    private ComboBox<String> placeHour;

    @FXML
    private ComboBox<String> placeMin;

     @FXML
    private Button deletePlaceButton;

    @FXML
    private TextField placeDestinationField;

    @FXML
    private TextField placeTimeField;

    public void initialize() {
        placeHour.setItems(optionsHour);
        placeMin.setItems(optionsMin);
        placeAMPM.setItems(optionsAMPM);
        placeHour.getSelectionModel().select("01");
        placeMin.getSelectionModel().select("00");
        placeAMPM.getSelectionModel().select("AM");
    }

    ObservableList<String> optionsHour = FXCollections.observableArrayList(
        IntStream.range(1, 13)
             .mapToObj(i -> String.format("%02d", i))
             .toArray(String[]::new)
    );

    ObservableList<String> optionsMin = FXCollections.observableArrayList(
    IntStream.range(0, 60)
             .mapToObj(i -> String.format("%02d", i))
             .toArray(String[]::new)
    );

    ObservableList<String> optionsAMPM = FXCollections.observableArrayList(
        "AM", "PM"
    );

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
            placesContainer.getChildren().add(placePanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TextField> getTimeFields() {
        return timeFields;
    }

    public List<TextField> getLocFields() {
        return locFields;
    }
}

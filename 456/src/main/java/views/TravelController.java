package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import design.RoundBorder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DatabaseHelper;

public class TravelController {

    @FXML
    private Button deleteItineraryBtn;

    @FXML
    private Button doneBtn;

    @FXML
    private Label travelName;

    @FXML
    private HBox travelPanelsContainer;

    private String travelNameText;

    public MainController mainController = new MainController();

    public String loggedInUsername;

    @FXML
    void doneTravel(ActionEvent event) {
        Button removeButton = (Button) event.getSource();

        Scene scene = removeButton.getScene();
        Parent rootPane = scene.getRoot();

        Pane windowTab = (Pane) rootPane.lookup("#windowTab");
        Text usernameLabel = (Text) windowTab.lookup("#usernameLabel");

        loggedInUsername = usernameLabel.getText();
        HBox travelPanelsContainer1 = (HBox) removeButton.getParent();
        VBox anchorHome1 = (VBox) travelPanelsContainer1.getParent();
        AnchorPane anchorHome = (AnchorPane) anchorHome1.getParent();

        Label travelNameLabel = (Label) travelPanelsContainer1.lookup("#travelName");
        String travelNameToDelete = travelNameLabel.getText();

        anchorHome1.getChildren().remove(travelPanelsContainer1);

        double currentHeight = anchorHome1.getPrefHeight();
        double removedHeight = travelPanelsContainer1.getPrefHeight();
        double newHeight = Math.max(currentHeight - removedHeight, 628);

        anchorHome.setPrefHeight(newHeight);
        anchorHome1.setPrefHeight(newHeight);
        travelPanelsContainer1.setPrefHeight(newHeight);

        boolean success = DatabaseHelper.markItineraryAsDone(loggedInUsername, travelNameToDelete);
        
        if (success) {
            showAlert("Success", "Travel marked as done successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to mark travel as done.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setUsername(String username) {
        loggedInUsername =  username;
    }
    public String getUsername() {
        return loggedInUsername;
    }

    @FXML
    void gotoShow(MouseEvent event) {
        HBox travelNameContain = (HBox) event.getSource();
        Scene scene = travelNameContain.getScene();
        Parent rootPane = scene.getRoot();

        Pane windowTab = (Pane) rootPane.lookup("#windowTab");
        Text usernameLabel = (Text) windowTab.lookup("#usernameLabel");

        loggedInUsername = usernameLabel.getText();

        VBox anchorHome1 = (VBox) travelNameContain.getParent();
        TabPane mainTabPane= (TabPane) anchorHome1.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
        Label travelNameLabel = (Label) travelNameContain.lookup("#travelName");
        String travelName = travelNameLabel.getText().trim();

        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals("Show")) {
                mainTabPane.getSelectionModel().select(tab);
                break;
            }
        }
        Platform.runLater(() -> {
            System.out.println("Switching to Show tab");
            try {
                List<String> itineraryDetails = DatabaseHelper.getItineraryByTravelName(loggedInUsername,travelName);
                if (itineraryDetails != null) {
                    System.out.println("Itinerary Details:");
                    System.out.println("Username: " + itineraryDetails.get(0));
                    System.out.println("Date: " + itineraryDetails.get(1));
                    System.out.println("Starting Location: " + itineraryDetails.get(3));
                    System.out.println("ETD: " + itineraryDetails.get(4));
                    System.out.println("Destination: " + itineraryDetails.get(5));
                    System.out.println("ETA: " + itineraryDetails.get(6));

                    for (Tab tab : mainTabPane.getTabs()) {
                        if (tab.getText().equals("Show")) {
            
                            Pane showTabContent = (Pane) tab.getContent();
                            ObservableList<Node> children = showTabContent.getChildren();
                            System.out.println("dasasdasd" + children);
            
                            for (Node node : children) {
                                if (node instanceof Pane) {
                                    Pane innerPane = (Pane) node;
                                    ObservableList<Node> innerChildren = innerPane.getChildren();
                                    for (Node innerNode : innerChildren) {
                                        if (innerNode instanceof GridPane) {
                                            GridPane gridPane = (GridPane) innerNode;
                                            DatePicker calendarField1 = (DatePicker) gridPane.lookup("#calendarField1");
                                            TextField travelNameField1 = (TextField) gridPane.lookup("#travelNameField1");
                                            TextField startingField1 = (TextField) gridPane.lookup("#startingField1");
                                            TextField etdField1 = (TextField) gridPane.lookup("#etdField1");
                                            TextField destinationPointField1 = (TextField) gridPane.lookup("#destinationPointField1");
                                            TextField etaField1 = (TextField) gridPane.lookup("#etaField1");

                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                                            LocalDate date = LocalDate.parse(itineraryDetails.get(1), formatter);
                                            
                                            calendarField1.setValue(date);
                                            travelNameField1.setText(travelName);
                                            startingField1.setText(itineraryDetails.get(3));
                                            etdField1.setText(itineraryDetails.get(4));
                                            destinationPointField1.setText(itineraryDetails.get(5));
                                            etaField1.setText(itineraryDetails.get(6));
            
                                            break;
                                        }
                                    }
                                }
                                if (node instanceof ScrollPane){
                                    ScrollPane scrollPane = (ScrollPane) node;

                                    Node scrollContent = scrollPane.getContent();
                                    if (scrollContent instanceof AnchorPane) {
                                        AnchorPane parentAnchorPane = (AnchorPane) scrollContent;
                                        ObservableList<Node> scrollChildren = parentAnchorPane.getChildren();

                                        for (Node child : scrollChildren) {
                                            if(child instanceof VBox){
                                                VBox dayVBox = (VBox) child;
                                                dayVBox.getChildren().clear();
                                                List<Integer> Days = DatabaseHelper.getDaysByTravelName(loggedInUsername, travelName);   
                                                try {
                                                    for (int day = 1; day <= Days.size(); day++) {
                                                        System.out.println(day);
                                                        List<String> placesPerDay = DatabaseHelper.getDestinationsByTravelAndDay(loggedInUsername,travelName,day);
                                                        for (int i = 0; i < placesPerDay.size(); i++){
                                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPhoto.fxml"));
                                                            HBox photoContainer = loader.load();
                                                            TextField travField = (TextField) loader.getNamespace().get("photoDestinationField");
                                                            travField.setText("Day " + day + " - " + placesPerDay.get(i));

                                                            double currentHeight_dayBoc = dayVBox.getPrefHeight();

                                                            double newHeight_dayVbox = currentHeight_dayBoc + photoContainer.getPrefHeight();

                                                            if (newHeight_dayVbox < 380) {
                                                                dayVBox.setPrefHeight(newHeight_dayVbox);
                                                            }else{
                                                                parentAnchorPane.setPrefHeight(newHeight_dayVbox);
                                                                dayVBox.setPrefHeight(newHeight_dayVbox);
                                                            }
                                                            dayVBox.getChildren().add(photoContainer);
                                                        }                                                       
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }        
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    System.out.println("No itinerary found with the travel name: " + travelName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    
    public void setTravelName(String name) {
        travelName.setText(name);
        travelNameText = name;
    }
    @FXML
    void deleteItinerary(ActionEvent event) throws IOException {
        Button removeButton = (Button) event.getSource();
        // Scene scene = removeButton.getScene();
        // Parent rootPane = scene.getRoot();

        // Pane windowTab = (Pane) rootPane.lookup("#windowTab");
        // Text usernameLabel = (Text) windowTab.lookup("#usernameLabel");

        // loggedInUsername = usernameLabel.getText();
        HBox travelPanelsContainer1 = (HBox) removeButton.getParent();
        VBox anchorHome1 = (VBox) travelPanelsContainer1.getParent();
        AnchorPane anchorHome = (AnchorPane) anchorHome1.getParent();

        Label travelNameLabel = (Label) travelPanelsContainer1.lookup("#travelName");
        String travelNameToDelete = travelNameLabel.getText();

        anchorHome1.getChildren().remove(travelPanelsContainer1);

        double currentHeight = anchorHome1.getPrefHeight();

        double removedHeight = travelPanelsContainer1.getPrefHeight();
        System.out.println("Removed Panel Height: " + removedHeight);

        double newHeight = currentHeight - removedHeight;

        double minHeight = 628;
        if (newHeight < minHeight) {
            newHeight = minHeight;
        }
        anchorHome.setPrefHeight(newHeight);
        anchorHome1.setPrefHeight(newHeight);
        travelPanelsContainer1.setPrefHeight(newHeight);

        if (mainController == null) {
            System.out.println("Error: MainController is not initialized.");
            return;
        }

        try {
            boolean deleted = DatabaseHelper.deleteItinerary(travelNameToDelete);
            if (deleted) {
                mainController.loadTravelPanels();;
            } else {
                System.out.println("Error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getTravelName() {
        return travelNameText;
    }
}

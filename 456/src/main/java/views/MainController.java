package views;

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
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DatabaseHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.events.Comment;

import design.RoundBorder;
import views.addDayController;

public class MainController {

    public String loggedInUsername;

    @FXML
    private Button addDayBtn;

    @FXML
    private AnchorPane anchorHome;

    @FXML
    private VBox anchorHome1;

    @FXML
    private VBox anchorTest;

    @FXML
    private AnchorPane anchorTest1;

    @FXML
    private DatePicker calendarField;

    @FXML
    private Button createBtn;

    @FXML
    private TextField destinationPointField;

    @FXML
    private TextField etaField;

    @FXML
    private TextField etdField;

    @FXML
    private Button exit;

    @FXML
    private Button historyBtn;

    @FXML
    private Button homepageBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Button saveBtn;

    @FXML
    private Button savePhotoBtn;

    @FXML
    private ScrollPane scrollPaneContent;

    @FXML
    private AnchorPane sidebarPane;

    @FXML
    private Button sidepanelBtn;

    @FXML
    private SplitPane splitPane;

    @FXML
    private TextField startingField;

    @FXML
    private Pane test;

    @FXML
    private TextField travelNameField;

    @FXML
    private addDayController addDayControllerInstance;

    @FXML
    public TravelController travelControllerInstance;

    public void initialize() {
        addDayControllerInstance = new addDayController();
        addPlaceControllerInstance = new addPlaceController();
        travelControllerInstance = new TravelController();
        loadTravelPanels();
    }

    void gotoShow(){
        selectTab("Show");
    }

    @FXML
    void goToCreate(ActionEvent event) {
        selectTab("Create");
    }

    @FXML
    void goToHistory(ActionEvent event) {
        selectTab("History");
    }

    @FXML
    void goToHomePage(ActionEvent event) {
        selectTab("Homepage");
        loadTravelPanels();
    }

    @FXML
    void logoutBtn(ActionEvent event) {
        logout();
    }

    @FXML
    void sideShower(ActionEvent event) {

    }

    void loadTravelPanels() {
        System.out.println("Start loading panels...");
        
        try {
            anchorHome1.getChildren().clear();
            System.out.println("Cleared anchorHome1 children.");
            anchorHome.setPrefHeight(0);
            anchorHome1.setPrefHeight(628);
            anchorHome.setMinHeight(628);
        
            // Fetch travel names from the database
            List<String> travelNames = DatabaseHelper.getNotDoneTravelNames("");         
            for (String travelName : travelNames) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/addTravel.fxml"));        
                HBox travelPanelsContainer = loader.load();        
                Label travelLabel = (Label) loader.getNamespace().get("travelName");
                travelLabel.setText(travelName);
        
                // Calculate the new height for the anchorHome1
                double currentHeight = anchorHome.getPrefHeight();

                System.out.println(currentHeight);
                double newHeight = currentHeight + travelPanelsContainer.getPrefHeight() + 20;

                System.out.println("new "  + newHeight);
        
                // Ensure the minimum height of 628
                if (newHeight < 628) {
                    newHeight = newHeight;
                }
                anchorHome1.setPrefHeight(newHeight);
                anchorHome.setPrefHeight(newHeight);

                anchorHome1.getChildren().add(travelPanelsContainer);
                System.out.println("Added travel panel for: " + travelName);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load travel panels: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
        }
        
        System.out.println("Finished loading panels.");
    }
        

    @FXML
    private void AddDayPanel() {
        addDayControllerInstance.AddDayPanel(anchorTest, anchorTest1);
    }

    @FXML
    private void removeDay(ActionEvent event){
        addDayControllerInstance.removeDay(event);
    }

    public void selectTab(String tabText) {
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(tabText)) {
                mainTabPane.getSelectionModel().select(tab);
                break;
            }
        }
        Platform.runLater(() -> {
        System.out.println("Switching to " + tabText + " tab");
    });
    }

    private void logout() {
        loggedInUsername = null;
        showAlert("Logout", "You have been logged out.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private VBox dayPanelsContainer;

    @FXML

    private addDayController addDayController;
    private addPlaceController addPlaceControllerInstance;

    @FXML
    private void saveToDatabase(ActionEvent event) {
        try (Connection conn = DatabaseHelper.getConnection()) { // Get a connection
            String travelName = travelNameField.getText().trim();
            String itineraryDate = calendarField.getEditor().getText().trim();
            String startingLocation = startingField.getText().trim();
            String etd = etdField.getText().trim();
            String destination = destinationPointField.getText().trim();
            String eta = etaField.getText().trim();

            // Validate required fields
            if (travelName.isEmpty() || itineraryDate.isEmpty() || startingLocation.isEmpty() ||
                etd.isEmpty() || destination.isEmpty() || eta.isEmpty()) {
                showAlert("Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
                return;
            }

            // Insert the itinerary into the database
            int itineraryId = DatabaseHelper.insertItinerary(conn, loggedInUsername, travelName, itineraryDate, startingLocation, etd, destination, eta);
            if (itineraryId == -1) {
                throw new SQLException("Failed to insert itinerary.");
            }

            List<VBox> dayPanelsContainers = addDayControllerInstance.getDayPanelsContainers();
            boolean dayAdded = false;

            for (VBox dayPanel : dayPanelsContainers) {
                Label dayLabel = (Label) dayPanel.lookup("#dayLabel");
                if (dayLabel == null) {
                    System.err.println("dayLabel is null in dayPanel: " + dayPanel);
                    continue;
                }
                String dayText = dayLabel.getText();
                System.out.println("Processing Day: " + dayText);

                // Insert day in the same connection context
                DatabaseHelper.insertDay(conn, itineraryId, Integer.parseInt(dayText.split(" ")[1]));
                int dayId = DatabaseHelper.getLastInsertId(conn);

                VBox placesContainer = (VBox) dayPanel.lookup("#placesContainer");
                if (placesContainer == null) {
                    System.err.println("placesContainer is null in dayPanel: " + dayPanel);
                    continue;
                }

                boolean placeAdded = false;

                for (int placeIndex = 0; placeIndex < placesContainer.getChildren().size(); placeIndex++) {
                    HBox placePanel = (HBox) placesContainer.getChildren().get(placeIndex);
                    TextField timeField = (TextField) placePanel.lookup("#placeTimeField");
                    TextField destinationField = (TextField) placePanel.lookup("#placeDestinationField");

                    if (timeField == null || destinationField == null) {
                        System.err.println("placeTimeField or placeDestinationField is null in placePanel: " + placePanel);
                        continue;
                    }

                    String placeTime = timeField.getText().trim();
                    String placeDestination = destinationField.getText().trim();
                    System.out.println("Place Time: " + placeTime + ", Destination: " + placeDestination);

                    if (placeTime.isEmpty()) {
                        placeTime = "Unknown Time";
                    }
                    if (placeDestination.isEmpty()) {
                        placeDestination = "Unknown Destination";
                    }

                    DatabaseHelper.insertPlace(conn, dayId, placeTime, placeDestination, null);
                    placeAdded = true;
                    timeField.clear();
                    destinationField.clear();
                }

                if (placeAdded) {
                    dayAdded = true;
                }
            }

            if (!dayAdded) {
                showAlert("Error", "Please add at least one day and one place to save the itinerary.", Alert.AlertType.ERROR);
                return;
            }

            // Clear fields after successful save
            travelNameField.clear();
            calendarField.getEditor().clear();
            startingField.clear();
            etdField.clear();
            destinationPointField.clear();
            etaField.clear();

            reload();

            // Show success message
            showAlert("Save Successful", "Data saved to database successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            // Handle any SQL exceptions
            showAlert("Error", "Failed to save data to database: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }



    @FXML
    void close_window(ActionEvent event) {
        ((Stage) exit.getScene().getWindow()).close();
    }

    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setUsername(String username) {
        loggedInUsername =  username;
    }

    public void reload(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            MainController dashboardController = loader.getController();
            dashboardController.setUsername(loggedInUsername);

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Dashboard");
            dashboardStage.setScene(new Scene(dashboardRoot));
            dashboardStage.show();

            ((Stage) createBtn.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void savePhotos(ActionEvent event) {

    }

}

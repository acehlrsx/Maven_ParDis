package views;

import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DatabaseHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

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

    @FXML
    private Text usernameLabel;

    @FXML
    private VBox dayPanelsContainer;

    @FXML
    private ComboBox<String> etaAMPM;

    @FXML
    private TextField etaField1;

    @FXML
    private ComboBox<String> etaHour;

    @FXML
    private ComboBox<String> etaMin;

    @FXML
    private ComboBox<String> etdAMPM;

    @FXML
    private TextField etdField1;

    @FXML
    private ComboBox<String> etdHour;

    @FXML
    private ComboBox<String> etdMin;

    @FXML
    private addDayController addDayController;

    @FXML
    private AnchorPane galleryHome;

    @FXML
    private VBox galleryHome1;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    ThreadPoolExecutor executor1 = (ThreadPoolExecutor) this.executor;

    // Get Statistics
    long completedTaskCount = executor1.getCompletedTaskCount();
    long taskCount = executor1.getTaskCount();
    int activeCount = executor1.getActiveCount();
    int poolSize = executor1.getPoolSize();
    int largestPoolSize = executor1.getLargestPoolSize();
    long keepAliveTime = executor1.getKeepAliveTime(TimeUnit.MILLISECONDS);

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

    public void initialize() {
        addDayControllerInstance = new addDayController();
        travelControllerInstance = new TravelController();
        etdHour.setItems(optionsHour);
        etdMin.setItems(optionsMin);
        etdAMPM.setItems(optionsAMPM);
        etaHour.setItems(optionsHour);
        etaMin.setItems(optionsMin);
        etaAMPM.setItems(optionsAMPM);
        etdHour.getSelectionModel().select("01");
        etdMin.getSelectionModel().select("00");
        etdAMPM.getSelectionModel().select("AM");
        etaHour.getSelectionModel().select("01");
        etaMin.getSelectionModel().select("00");
        etaAMPM.getSelectionModel().select("AM");

        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    Platform.runLater(() -> updateExecutorStatusUI());
                    Thread.sleep(1000); 
                }
                return null;
            }
        };

        Thread updateThread = new Thread(updateTask);
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateExecutorStatusUI() {
        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executor;
        String stats = String.format(
            "Completed Tasks: %d\nTotal Tasks: %d\nActive Threads: %d\nPool Size: %d\nLargest Pool Size: %d\n",
            poolExecutor.getCompletedTaskCount(),
            poolExecutor.getTaskCount(),
            poolExecutor.getActiveCount(),
            poolExecutor.getPoolSize(),
            poolExecutor.getLargestPoolSize()
        );
        System.err.println(stats);
    }
    

    void gotoShow(){
        selectTab("Show");
    }

    @FXML
    void goToCreate(ActionEvent event) {
        selectTab("Create");
        addDayControllerInstance.setDayCount(0);
        addDayControllerInstance.clearAnchorTest(anchorTest);
    }

    @FXML
    void goToHistory(ActionEvent event) {
        selectTab("History");
        loadDoneTravels();
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
        executor.submit(() -> { 
            try {
                anchorHome1.getChildren().clear();
                anchorHome1.setPrefHeight(628);
                anchorHome.setMinHeight(628);
            
                List<String> travelNames = DatabaseHelper.getNotDoneTravelNames(loggedInUsername);         
                for (String travelName : travelNames) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/addTravel.fxml"));        
                    HBox travelPanelsContainer = loader.load();        
                    Label travelLabel = (Label) loader.getNamespace().get("travelName");
                    travelLabel.setText(travelName);
            
                    double currentHeight = anchorHome.getPrefHeight();

                    double newHeight = currentHeight + travelPanelsContainer.getPrefHeight() + 20;
            
                    if (newHeight < 628) {
                        newHeight = newHeight;
                    }
                    anchorHome1.setPrefHeight(newHeight);
                    anchorHome.setPrefHeight(newHeight);

                    anchorHome1.getChildren().add(travelPanelsContainer);
                }
                
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to load travel panels: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                });
            }
        });      
    }

    void loadDoneTravels() {
        executor.submit(() -> {
            try {
                galleryHome1.getChildren().clear();
                galleryHome1.setPrefHeight(628);
                galleryHome.setMinHeight(628);
            
                List<String> travelNamesDone = DatabaseHelper.getDoneTravelNames(loggedInUsername);   
                for (String travelNameDone : travelNamesDone) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/addHistory.fxml"));        
                    HBox historyPanelContainer = loader.load();      

                    Label travelHistory = (Label) loader.getNamespace().get("travelHistory");
                    travelHistory.setText(travelNameDone);
            
                    double currentHeight = galleryHome.getPrefHeight();

                    double newHeight = currentHeight + historyPanelContainer.getPrefHeight() + 20;
            
                    if (newHeight < 628) {
                        newHeight = newHeight;
                    }
                    galleryHome1.setPrefHeight(newHeight);
                    galleryHome.setPrefHeight(newHeight);

                    galleryHome1.getChildren().add(historyPanelContainer);
                }
            } catch (IOException e) {
                showAlert("Error", "Failed to load travel panels: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
            }
            System.out.println("Finished loading panels.");
        });
    }
        

    @FXML
    private void AddDayPanel() {
        executor.submit(() -> { 
            addDayControllerInstance.AddDayPanel(anchorTest, anchorTest1);
        });
    }

    @FXML
    private void removeDay(ActionEvent event){
        executor.submit(() -> { 
            addDayControllerInstance.removeDay(event);
        });
    }

    public void selectTab(String tabText) {
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(tabText)) {
                mainTabPane.getSelectionModel().select(tab);
                break;
            }
        }
    }

    private void logout() {
        loggedInUsername = null;
        showAlert("Logout", "You have been logged out.", Alert.AlertType.INFORMATION);
        ((Stage) exit.getScene().getWindow()).close();
    }

    int day = 1;
    @FXML
    private void saveToDatabase(ActionEvent event) {
        executor.submit(() -> { 
            try (Connection conn = DatabaseHelper.getConnection()) {
                String emailReceiveString = DatabaseHelper.getEmailByUsername(loggedInUsername);
                anchorTest.getChildren().clear();

                String etdHouString = etdHour.getValue();
                String etdMinString = etdMin.getValue();
                String etdAMPMString = etdAMPM.getValue();

                String etaHourString = etaHour.getValue();
                String etaMinString = etaMin.getValue();
                String etaAMPMString = etaAMPM.getValue();
                
                String travelName = travelNameField.getText().trim();
                String itineraryDate = calendarField.getEditor().getText().trim();
                String startingLocation = startingField.getText().trim();
                String etd = etdHouString + ":" + etdMinString + etdAMPMString;
                String destination = destinationPointField.getText().trim();
                String eta = etaHourString + ":" + etaMinString + etaAMPMString;

                if (travelName.isEmpty() || itineraryDate.isEmpty() || startingLocation.isEmpty() || etd.isEmpty() || destination.isEmpty() || eta.isEmpty()) {
                    showAlert("Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
                    return;
                }

                List<VBox> dayPanelsContainers = addDayControllerInstance.getDayPanelsContainers();
                if (dayPanelsContainers.isEmpty()) {
                    showAlert("Error", "Please add at least one day to your itinerary.", Alert.AlertType.ERROR);
                    return;
                }

                if (!itineraryDate.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                    showAlert("Error", "Invalid date format. Please use M/d/yyyy.", Alert.AlertType.ERROR);
                    return;
                }

                int itineraryId = DatabaseHelper.insertItinerary(conn, loggedInUsername, travelName, itineraryDate, startingLocation, etd, destination, eta);
                if (itineraryId == -1) {
                    throw new SQLException("Failed to insert itinerary.");
                }

                boolean hasValidDay = false;

                for (VBox dayPanel : dayPanelsContainers) {
                    Label dayLabel = (Label) dayPanel.lookup("#dayLabel");
                    if (dayLabel == null) {
                        System.err.println("dayLabel is null in dayPanel: " + dayPanel);
                        continue;
                    }
                    String dayText = dayLabel.getText();

                    boolean placeAdded = false;
                    DatabaseHelper.insertDay(conn, itineraryId, Integer.parseInt(dayText.split(" ")[1]));
                    int dayId = DatabaseHelper.getLastInsertId(conn);

                    VBox placesContainer = (VBox) dayPanel.lookup("#placesContainer");
                    if (placesContainer == null) {
                        System.err.println("placesContainer is null in dayPanel: " + dayPanel);
                        return; 
                    }

                    if (placesContainer.getChildren().isEmpty()) {
                        showAlert("Error", "Please add at least one place for Day " + dayText, Alert.AlertType.ERROR);
                        return;
                    }
                    for (Node placeNode : placesContainer.getChildren()) {
                        if (placeNode instanceof HBox) {
                            HBox placePanel = (HBox) placeNode;

                            ComboBox<String> comHour = (ComboBox<String>) placePanel.lookup("#placeHour");
                            ComboBox<String> comMin = (ComboBox<String>) placePanel.lookup("#placeMin");
                            ComboBox<String> comAMPM = (ComboBox<String>) placePanel.lookup("#placeAMPM");

                            String hourPlace = comHour.getValue();
                            String minPlace = comMin.getValue();
                            String AMPMPlace = comAMPM.getValue();

                            TextField destinationField = (TextField) placePanel.lookup("#placeDestinationField");
                            String placeTime = hourPlace + ":" + minPlace + AMPMPlace;
                            String placeDestination = destinationField.getText().trim();

                            if (placeTime == null || placeTime.isEmpty()) {
                                placeTime = "Unknown Time";
                            }
                            if (placeDestination.isEmpty()) {
                                placeDestination = "Unknown Destination";
                            }

                            DatabaseHelper.insertPlace(conn, dayId, placeTime, placeDestination, null);
                            placeAdded = true;
                            hasValidDay = true; 
                        }
                    }

                    if (!placeAdded) {
                        showAlert("Error", "Please add at least one place for Day " + dayText, Alert.AlertType.ERROR);
                        DatabaseHelper.deleteDayById(conn, dayId);
                        return;
                    }
                }
                
                if (!hasValidDay) {
                    showAlert("Error", "Please add at least one day with one place to your itinerary.", Alert.AlertType.ERROR);
                    DatabaseHelper.deleteItineraryById(conn, itineraryId);
                    return;
                }

                travelNameField.clear();
                calendarField.getEditor().clear();
                startingField.clear();
                destinationPointField.clear();

                DatabaseHelper.generateItineraryPaper(itineraryId, emailReceiveString);

                goToHomePage(event);

                showAlert("Save Successful", "Data saved to database successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to save data to database: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
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
        usernameLabel.setText(username);
        loadTravelPanels();
    }
    public String getUsername() {
        return loggedInUsername;
    }
    @FXML
    void savePhotos(ActionEvent event){
    }
}

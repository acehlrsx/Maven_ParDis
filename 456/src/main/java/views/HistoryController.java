package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.DatabaseHelper;

public class HistoryController {

    @FXML
    private HBox historyPanelContainer;

    @FXML
    private Label travelHistory;

    @FXML
    private Button viewGalleryBtn;

    public String loggedInUsername;

    @FXML
    void gotoShow(MouseEvent event) {

    }

    @FXML
    void viewGallery(ActionEvent event) {
        Button viewGalleryBtn = (Button) event.getSource();
        Scene scene = viewGalleryBtn.getScene();
        Parent rootPane = scene.getRoot();

        Pane windowTab = (Pane) rootPane.lookup("#windowTab");
        Text usernameLabel = (Text) windowTab.lookup("#usernameLabel");

        loggedInUsername = usernameLabel.getText();
        System.err.println(loggedInUsername);

        HBox historyPanelContainer = (HBox) viewGalleryBtn.getParent();

        VBox galleryHome1 = (VBox) historyPanelContainer.getParent();
        TabPane mainTabPane = (TabPane) galleryHome1.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
        Label travelHistoryNameLabel = (Label) historyPanelContainer.lookup("#travelHistory");
        String travelName = travelHistoryNameLabel.getText().trim();

        System.err.println(travelName);

        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals("Gallery")) {
                mainTabPane.getSelectionModel().select(tab);
                break;
            }
        }
        Platform.runLater(() -> {
            System.out.println("Switching to Gallery tab");
            try {
                List<String> itineraryDetails = DatabaseHelper.getItineraryByTravelName(loggedInUsername,travelName);
                if (itineraryDetails != null) {
                    for (Tab tab : mainTabPane.getTabs()) {
                        if (tab.getText().equals("Gallery")) {
            
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
                                            DatePicker calendarField1 = (DatePicker) gridPane.lookup("#calendarField11");
                                            TextField travelNameField1 = (TextField) gridPane.lookup("#travelNameField11");
                                            TextField startingField1 = (TextField) gridPane.lookup("#startingField11");
                                            TextField etdField1 = (TextField) gridPane.lookup("#etdField11");
                                            TextField destinationPointField1 = (TextField) gridPane.lookup("#destinationPointField11");
                                            TextField etaField1 = (TextField) gridPane.lookup("#etaField11");

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
                                                VBox gridPhoto = (VBox) child;
                                                gridPhoto.getChildren().clear();
                                                List<Integer> Days = DatabaseHelper.getDaysByTravelName(loggedInUsername, travelName); 
                                                int itineraryId = DatabaseHelper.getItineraryIdByTravelName(loggedInUsername,travelName);  
                                                try {
                                                    for (int day = 1; day <= Days.size(); day++) {
                                                        System.out.println(day);
                                                        List<String> placesPerDay = DatabaseHelper.getDestinationsByTravelAndDay(loggedInUsername,travelName,day);

                                                        for (int i = 0; i < placesPerDay.size(); i++){
                                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GalleryPerDay.fxml"));
                                                            VBox photoCont = loader.load();
                                                            Text travField = (Text) loader.getNamespace().get("dayPhoto");
                                                            ImageView imagePerDay = (ImageView) loader.getNamespace().get("imagePerDay");
                                                            travField.setText("Day " + day + " - " + placesPerDay.get(i));

                                                            String photoString = DatabaseHelper.getPhotoPath(loggedInUsername, placesPerDay.get(i), day, itineraryId);

                                                            photoString = photoString.replaceAll("\\\\", "/");
                                                            int index = photoString.lastIndexOf("resources") + "resources".length();
                                                            String relativePath = photoString.substring(index);

                                                            Image image = new Image(getClass().getResourceAsStream(relativePath));
                                                            imagePerDay.setImage(image);

                                                            double currentHeight_dayBoc = gridPhoto.getPrefHeight();
                                                            double newHeight_gridPhoto = currentHeight_dayBoc + photoCont.getPrefHeight();
                                                            if (newHeight_gridPhoto < 644) {
                                                                gridPhoto.setPrefHeight(newHeight_gridPhoto);
                                                            }else{
                                                                parentAnchorPane.setPrefHeight(newHeight_gridPhoto);
                                                                gridPhoto.setPrefHeight(newHeight_gridPhoto);
                                                            }
                                                            gridPhoto.getChildren().add(photoCont);
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
}

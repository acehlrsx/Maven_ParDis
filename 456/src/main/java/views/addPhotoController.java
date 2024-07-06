package views;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import model.DatabaseHelper;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class addPhotoController {

    @FXML
    private TextField photoDestinationField;

    @FXML
    private Button uploadPlaceButton;

    public MainController maincontrol = new MainController();

    public String loggedInUsername;

    public void setUsername(String username) {
        loggedInUsername =  username;
    }

    @FXML
    void uploadPlace(ActionEvent event) {
        Button uploadBtn = (Button) event.getSource();

        Scene scene = uploadBtn.getScene();
        Parent rootPane = scene.getRoot();

        Pane windowTab = (Pane) rootPane.lookup("#windowTab");
        Text usernameLabel = (Text) windowTab.lookup("#usernameLabel");

        loggedInUsername = usernameLabel.getText();

        HBox innerphotoContainer = (HBox) uploadBtn.getParent();
        Pane photoContainer = (Pane) uploadBtn.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
        TextField photoPathField = (TextField) innerphotoContainer.lookup("#photoPathField");
        ObservableList<Node> Children = photoContainer.getChildren();

        for (Node innerChildren : Children) {
            if (innerChildren instanceof Pane) {
                Pane innerPane = (Pane) innerChildren;
                ObservableList<Node> paneChildren = innerPane.getChildren();
                for(Node paneChild : paneChildren){
                    if(paneChild instanceof GridPane){
                        GridPane gridPane = (GridPane) paneChild;
                        TextField travelNameField1 = (TextField) gridPane.lookup("#travelNameField1");
                        String travelNameString = travelNameField1.getText();

                        String destination = photoDestinationField.getText();
                        String[] parts = destination.split("-");
                        String place = parts[1].trim();
                        String[] days = parts[0].split(" ");
                        int dayNumber = Integer.parseInt(days[1]);

                        try {
                            int itineraryId = DatabaseHelper.getItineraryIdByTravelName(loggedInUsername,travelNameString);
                            int dayId = DatabaseHelper.getDayId(itineraryId, dayNumber);
                            System.out.println("ITI " +  itineraryId);
                            System.out.println("DAY " +  dayId);
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Select Photo");

                            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                                "Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"
                            );
                            fileChooser.getExtensionFilters().add(imageFilter);
                            
                            Path targetDirectory = Paths.get("Maven_ParDis/456/src/main/resources/travelPhotos");
                            File selectedFile = fileChooser.showOpenDialog(photoContainer.getScene().getWindow());

                            if (selectedFile != null) {
                                String originalPhotoPath = selectedFile.getAbsolutePath(); 
                                photoPathField.setText(originalPhotoPath);

                                String fileName = travelNameString + "-" + destination + "_" + selectedFile.getName();

                                System.err.println(fileName);

                                Path targetPath = targetDirectory.resolve(fileName); 
                                String pathTarget = targetPath.toString();

                                try {
                                    DatabaseHelper.updatePhotoDetails(loggedInUsername,place, pathTarget, itineraryId, dayId);
                                    Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING); 
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            } 
            break;
        }
    }
}

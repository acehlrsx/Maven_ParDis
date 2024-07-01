package views;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    private String selectedPhotoPath;

    @FXML
    void uploadPlace(ActionEvent event) {
        Button uploadBtn = (Button) event.getSource();
        Pane photoContainer = (Pane) uploadBtn.getParent().getParent().getParent().getParent().getParent().getParent().getParent();

        System.out.println(photoContainer);

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
                        String[] days = parts[0].split(" ");
                        int dayNumber = Integer.parseInt(days[1]);

                        System.out.println(dayNumber);

                        // try {
                        //     int itineraryId = DatabaseHelper.getItineraryIdByTravelName(travelNameString);
                        //     int dayId = DatabaseHelper.getDayId(itineraryId, dayNumber);
                        // } catch (SQLException e) {
                        //     e.printStackTrace();
                        // }
                    }
                    break;
                }
            } 
            break;
        }


        // FileChooser fileChooser = new FileChooser();
        // fileChooser.setTitle("Select Photo to Upload");
        // File selectedFile = fileChooser.showOpenDialog(new Stage()); // Show file chooser dialog

        // if (selectedFile != null) {
        //     try {
        //         // Create a copy of the selected file
        //         File copiedFile = copyPhoto(selectedFile);

        //         // Check if copy was successful
        //         if (copiedFile != null && copiedFile.exists()) {
        //             // Update the database with the copied photo details
        //             String destination = photoDestinationField.getText().trim();
        //             String[] parts = destination.split("-");
        //             String placeName = parts[1].trim();

        //             System.out.println(placeName);
        //             String photoPath = copiedFile.getAbsolutePath();

        //             System.out.println(photoPath);

        //             try {
        //                 DatabaseHelper.updatePhotoDetails(placeName, photoPath);
        //             } catch (SQLException e) {
        //                 e.printStackTrace();
        //             }
        //         } else {
        //             System.out.println("Failed to copy the photo.");
        //         }
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }
    }
    private File copyPhoto(File originalFile) throws IOException {
        // Define a directory for storing copied photos
        Path targetDirectory = Paths.get("Maven_ParDis/456/db/travelPhotos"); // Replace with your desired directory path

        // Create target directory if it doesn't exist
        if (!Files.exists(targetDirectory)) {
            Files.createDirectories(targetDirectory);
        }

        // Generate a unique file name for the copied photo
        String fileName = "copied_photo_" + System.currentTimeMillis() + "_" + originalFile.getName();
        Path copiedFilePath = targetDirectory.resolve(fileName);

        // Perform the file copy operation
        Files.copy(originalFile.toPath(), copiedFilePath, StandardCopyOption.REPLACE_EXISTING);

        return copiedFilePath.toFile();
    }
}

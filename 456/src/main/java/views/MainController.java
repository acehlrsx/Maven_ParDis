package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainController {

    @FXML
    private Button log_button;

    @FXML
    private PasswordField log_pass;

    @FXML
    private TextField log_user;

    @FXML
    private CheckBox show_check;

    @FXML
    void LogBtnOnClicked(ActionEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent signUpRoot = loader.load();

        // Optional: Get the SignUpController instance
        // SignUpController signUpController = loader.getController();

        // Create a new scene for the sign-up form
        Scene signUpScene = new Scene(signUpRoot);
        signUpScene.setFill(Color.TRANSPARENT);
        Rectangle roundedRect = new Rectangle(1280, 720); 
        roundedRect.setArcWidth(20);
        roundedRect.setArcHeight(20);
        signUpScene.getRoot().setClip(roundedRect);

        // Get the stage (window) from the login button
        Stage currentStage = (Stage) log_button.getScene().getWindow();

        // Set the sign-up scene on the current stage (replace the login scene)
        currentStage.setScene(signUpScene);
        currentStage.setTitle("Sign Up");
    }

    @FXML
    private CheckBox sign_check;

    @FXML
    private TextField sign_email;

    @FXML
    private Button sign_log;

    @FXML
    private TextField sign_name;

    @FXML
    private PasswordField sign_pass;

    @FXML
    private PasswordField sign_repass;

    @FXML
    private Button sign_txt;

    @FXML
    private TextField sign_user;

    @FXML
    void SignBtnOnClicked(ActionEvent event) {

    }

}

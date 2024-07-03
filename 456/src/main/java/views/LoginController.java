package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DatabaseHelper;

import java.sql.SQLException;

import design.RoundBorder;

public class LoginController {
    @FXML
    private Button exit;
// ==================Login Controller==================
    @FXML
    private Button goSignupBtn;

    @FXML
    private Button log_button;

    @FXML
    private PasswordField log_pass_hidden;

    @FXML
    private TextField log_pass_shown;

    @FXML
    private TextField log_user;

    @FXML
    private CheckBox show_check;

    @FXML
    void LogBtnOnClicked(ActionEvent event) throws Exception{
        String username = log_user.getText();
        String password = log_pass_hidden.getText();

        try {
            boolean loginSuccess = DatabaseHelper.authenticateUser(username, password);

            if (loginSuccess) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                Parent dashboardRoot = loader.load();
                
                MainController dashboardController = loader.getController();
                dashboardController.setUsername(username);

                Stage dashboardStage = new Stage();
                RoundBorder.setRoundedWindow(dashboardStage, dashboardRoot);
                dashboardStage.setTitle("Dashboard");
                dashboardStage.show();

                ((Stage) log_button.getScene().getWindow()).close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password.");
                alert.showAndWait();

                log_pass_hidden.clear();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred during login.");
            alert.showAndWait();
        }
    }
    @FXML
    void goSignup(ActionEvent event)  throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent signUpRoot = loader.load();

        Stage signUpStage = new Stage(); 
        RoundBorder.setRoundedWindow(signUpStage, signUpRoot);
        signUpStage.setTitle("Sign Up");
        signUpStage.show();

        ((Stage) goSignupBtn.getScene().getWindow()).close();
    }
    @FXML
    void handleShowPassword_in_Log(ActionEvent event) {
        if (show_check.isSelected()) {
            log_pass_shown.setText(log_pass_hidden.getText());
            log_pass_shown.setVisible(true);
            log_pass_hidden.setVisible(false);
        } else {
            log_pass_hidden.setText(log_pass_shown.getText());
            log_pass_hidden.setVisible(true);
            log_pass_shown.setVisible(false);
        }
    }
    @FXML
    void close_window(ActionEvent event) {
        ((Stage) exit.getScene().getWindow()).close();
    }
    
}

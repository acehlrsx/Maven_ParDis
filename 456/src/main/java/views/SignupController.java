package views;

import java.sql.SQLException;
import java.util.regex.Pattern;

import design.RoundBorder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DatabaseHelper;

public class SignupController {
    @FXML
    private Button goLoginBtn;

    @FXML
    private CheckBox sign_check;

    @FXML
    private TextField sign_email;

    @FXML
    private Button sign_log;

    @FXML
    private TextField sign_name;

    @FXML
    private PasswordField sign_pass_hidden;

    @FXML
    private TextField sign_pass_shown;

    @FXML
    private PasswordField sign_repass_hidden;

    @FXML
    private TextField sign_repass_shown;

    @FXML
    private TextField sign_user;

    @FXML
    private Button exit;

    @FXML
    void SignBtnOnClicked(ActionEvent event)   throws Exception{
        String username = sign_user.getText().trim();
        String email = sign_email.getText().trim();
        String name = sign_name.getText().trim();
        String password = sign_pass_hidden.getText();
        String reenterPassword = sign_repass_hidden.getText();

        if (username.isEmpty() || email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Error", "Invalid email format.", Alert.AlertType.ERROR);
            return;
        }

        if (password.length() < 8) {
            showAlert("Error", "Password must be at least 8 characters long.", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(reenterPassword)) {
            showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        try {
            DatabaseHelper.createTable(); // Ensure the table exists (optional)
            boolean success = DatabaseHelper.insertUser(username, email, name, password);
            if (success) {
                showAlert("Success", "Sign Up Successful!", Alert.AlertType.INFORMATION);
                goLogin(event);
            } else {
                showAlert("Error", "Username already exists.", Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred during sign up.", Alert.AlertType.ERROR);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
        Parent signUpRoot = loader.load();

        Stage signUpStage = new Stage(); 
        RoundBorder.setRoundedWindow(signUpStage, signUpRoot);
        signUpStage.setTitle("Login");
        signUpStage.show();

        ((Stage) sign_log.getScene().getWindow()).close();
    }

    @FXML
    void goLogin(ActionEvent event)   throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent signUpRoot = loader.load();

        Stage signUpStage = new Stage(); 
        RoundBorder.setRoundedWindow(signUpStage, signUpRoot);
        signUpStage.setTitle("Login");
        signUpStage.show();

        ((Stage) goLoginBtn.getScene().getWindow()).close();
    }
    @FXML
    void handleShowPassword_in_Signup(ActionEvent event) {
        if (sign_check.isSelected()) {
            sign_pass_shown.setText(sign_pass_hidden.getText());
            sign_pass_shown.setVisible(true);
            sign_pass_hidden.setVisible(false);

            sign_repass_shown.setText(sign_repass_hidden.getText());
            sign_repass_shown.setVisible(true);
            sign_repass_hidden.setVisible(false);
        } else {
            sign_pass_hidden.setText(sign_pass_shown.getText());
            sign_pass_hidden.setVisible(true);
            sign_pass_shown.setVisible(false);

            sign_repass_hidden.setText(sign_repass_shown.getText());
            sign_repass_hidden.setVisible(true);
            sign_repass_shown.setVisible(false);
        }
    }
    private boolean isValidEmail(String email) {
        final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        return EMAIL_PATTERN.matcher(email).matches();
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void close_window(ActionEvent event) {
        ((Stage) exit.getScene().getWindow()).close();
    }
}

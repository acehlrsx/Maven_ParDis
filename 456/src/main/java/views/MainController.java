package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    void LogBtnOnClicked(ActionEvent event) {
        String username = log_user.getText();
        String password = log_pass.getText();

        if (show_check.isSelected()) {
            System.out.println("Username: " + username + ", Password: " + password);
        } else {
            System.out.println("Username: " + username + ", Password: *********");
        }
    }

}

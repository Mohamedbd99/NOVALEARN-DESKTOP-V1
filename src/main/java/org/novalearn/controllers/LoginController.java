package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.novalearn.MainApp;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void onLoginClicked() {
        // TODO: add real authentication via DatabaseConnection
        String user = usernameField.getText();
        String pass = passwordField.getText();
        if (!user.isEmpty() && !pass.isEmpty()) {
            try {
                MainApp.showAccueil();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // show warning, etc.
        }
    }
}

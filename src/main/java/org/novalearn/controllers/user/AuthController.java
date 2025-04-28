package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Les mots de passe ne correspondent pas.");
            return;
        }

        // Logique d'enregistrement (à compléter)
        showAlert("Inscription réussie !");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inscription");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

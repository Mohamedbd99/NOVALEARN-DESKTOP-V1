package org.novalearn.controllers.user;

import org.novalearn.MainApp;
import org.novalearn.services.quiz.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.TextField;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    private final UserService userService = new UserService();

    @FXML
    private void handleSend() {
        String email = emailField.getText();

        if (userService.emailExists(email)) {
            String newPassword = userService.resetPassword(email); // Génère un nouveau mot de passe
            showAlert(AlertType.INFORMATION, "Mot de passe temporaire envoyé", "Un mot de passe temporaire a été envoyé à votre adresse e-mail.");
        } else {
            showAlert("Erreur", "Email introuvable !");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Laisse cette ligne si tu ne veux pas de texte d'en-tête
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void onBackClicked() {
        try {
            MainApp.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.novalearn.MainApp;
import org.novalearn.services.quiz.UserService;

public class VerifyEmailController {
    @FXML
    private TextField verificationCodeField;

    private final UserService userService;
    private String email;

    public VerifyEmailController() {
        this.userService = new UserService();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void handleVerify() {
        String code = verificationCodeField.getText().trim();

        if (code.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer le code de vérification", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (userService.verifyUser(email, code)) {
                showAlert("Succès", "Votre compte a été vérifié avec succès", Alert.AlertType.INFORMATION);
                MainApp.showLogin();
            } else {
                showAlert("Erreur", "Code de vérification incorrect", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            MainApp.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

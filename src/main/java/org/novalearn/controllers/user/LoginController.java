package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.novalearn.MainApp;
import org.novalearn.services.quiz.UserService;

import java.sql.SQLException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @FXML
    private void onLoginClicked() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (userService.authenticate(email, password) != null) {
                if (userService.authenticate(email, password).getRole().equals("admin")) {
                    try {
                        MainApp.showAdminDashboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        MainApp.showAccueil();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                showAlert("Erreur", "Email ou mot de passe incorrect", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la connexion", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onRegisterClicked() {
        try {
            MainApp.showChooseRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            MainApp.showForgotPassword();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue", Alert.AlertType.ERROR);
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
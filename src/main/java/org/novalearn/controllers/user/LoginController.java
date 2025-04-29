package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.novalearn.MainApp;
import org.novalearn.services.quiz.FailedLoginService;
import org.novalearn.services.quiz.UserService;
import org.novalearn.Entity.User;

import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;

    private final UserService userService;
    private final FailedLoginService failedLoginService;

    public LoginController() {
        this.userService = new UserService();
        this.failedLoginService = new FailedLoginService();
    }

    // Méthode d'initialisation pour pré-remplir les champs avec les informations mémorisées
    @FXML
    public void initialize() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        String rememberedEmail = prefs.get("rememberedEmail", "");
        String rememberedPassword = prefs.get("rememberedPassword", "");

        if (!rememberedEmail.isEmpty() && !rememberedPassword.isEmpty()) {
            emailField.setText(rememberedEmail);
            passwordField.setText(rememberedPassword);
            rememberMeCheckBox.setSelected(true);
        }
    }

    @FXML
    private void onLoginClicked() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Vérifier si le compte est bloqué
            if (failedLoginService.isAccountLocked(email)) {
                showAlert("Erreur", "Votre compte est temporairement verrouillé. Veuillez réessayer plus tard.", Alert.AlertType.ERROR);
                return;
            }

            // Authentifier l'utilisateur
            User user = userService.authenticate(email, password);

            if (user != null) {
                // Réinitialiser les tentatives échouées après une connexion réussie
                failedLoginService.resetFailedAttempts(email);

                // Vérifier l'état du compte
                if (user.isActive() == 1) {
                    // Sauvegarder les informations de connexion si "Se souvenir de moi" est coché
                    if (rememberMeCheckBox.isSelected()) {
                        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
                        prefs.put("rememberedEmail", email);
                        prefs.put("rememberedPassword", password);  // Attention à la sécurité pour le mot de passe
                    }

                    // Rediriger vers la page appropriée en fonction du rôle de l'utilisateur
                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        MainApp.showAdminDashboard();
                    } else {
                        MainApp.showAccueil();
                    }
                } else {
                    // Utilisateur désactivé : connexion refusée
                    showAlert("Erreur", "Votre compte est désactivé. Veuillez contacter l'administrateur.", Alert.AlertType.ERROR);
                }
            } else {
                // Enregistrer un échec de connexion
                failedLoginService.recordFailedAttempt(email);
                showAlert("Erreur", "Email ou mot de passe incorrect.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la connexion.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur inattendue.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onRegisterClicked() {
        try {
            MainApp.showChooseRegister();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir l'écran d'inscription.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            MainApp.showForgotPassword();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir l'écran de réinitialisation du mot de passe.", Alert.AlertType.ERROR);
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

package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.novalearn.MainApp;
import org.novalearn.controllers.AccueilController;
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
            if (failedLoginService.isAccountLocked(email)) {
                showAlert("Erreur", "Votre compte est temporairement verrouillé. Veuillez réessayer plus tard.", Alert.AlertType.ERROR);
                return;
            }

            User user = userService.authenticate(email, password);

            if (user != null) {
                System.out.println("✅ Authenticated user: " + user.getEmail() + ", ID: " + user.getId());
                failedLoginService.resetFailedAttempts(email);

                if (user.isActive() == 1) {
                    if (rememberMeCheckBox.isSelected()) {
                        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
                        prefs.put("rememberedEmail", email);
                        prefs.put("rememberedPassword", password); // Consider encryption
                    }

                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        System.out.println("Redirecting to Admin Dashboard");
                        MainApp.showAdminDashboard();
                    } else {
                        System.out.println("➡️ Redirecting to Accueil.fxml");
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/novalearn/acceauil-views/accueil.fxml"));
                        Parent root = loader.load();

                        AccueilController accueilController = loader.getController();
                        accueilController.setCurrentUser(user);
                        System.out.println("📦 Passed user to AccueilController: " + user.getEmail());

                        Stage stage = (Stage) emailField.getScene().getWindow();
                        Scene scene = new Scene(root, 1600, 800); // Set fixed width and height
                        stage.setScene(scene);
                        stage.setWidth(1600);   // Optional, but ensures window size
                        stage.setHeight(800);  // Optional, ensures window size
                        stage.setResizable(false); // Optional: prevent resizing
                        stage.show();

                    }
                } else {
                    showAlert("Erreur", "Votre compte est désactivé. Veuillez contacter l'administrateur.", Alert.AlertType.ERROR);
                }
            } else {
                System.out.println("❌ Authentication failed for email: " + email);
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

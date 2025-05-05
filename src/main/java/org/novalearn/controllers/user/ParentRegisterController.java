package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.novalearn.Entity.User;
import org.novalearn.MainApp;
import org.novalearn.services.quiz.UserService;

import java.sql.SQLException;

public class ParentRegisterController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField numTelField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private TextField specialiteField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ProgressBar passwordStrengthBar;  // Ajout de la ProgressBar

    private final UserService userService;

    public ParentRegisterController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        genreComboBox.getItems().addAll("Homme", "Femme");

        // Ajouter un écouteur d'événements pour le champ de mot de passe
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> checkPasswordStrength());
    }

    // Fonction pour vérifier la force du mot de passe et mettre à jour la barre de progression
    private void checkPasswordStrength() {
        String password = passwordField.getText();
        int strength = 0;

        // Vérifications de complexité du mot de passe
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++; // Lettre majuscule
        if (password.matches(".*[a-z].*")) strength++; // Lettre minuscule
        if (password.matches(".*\\d.*")) strength++; // Chiffre
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strength++; // Caractère spécial

        // Mettre à jour la barre de progression
        passwordStrengthBar.setProgress(strength / 5.0); // La force va de 0 à 1 (5 critères max)

        // Mise à jour de la couleur de la barre en fonction de la force du mot de passe
        if (strength == 1) {
            passwordStrengthBar.setStyle("-fx-accent: red;");
        } else if (strength == 2) {
            passwordStrengthBar.setStyle("-fx-accent: orange;");
        } else if (strength == 3) {
            passwordStrengthBar.setStyle("-fx-accent: yellow;");
        } else if (strength == 4) {
            passwordStrengthBar.setStyle("-fx-accent: lightgreen;");
        } else if (strength == 5) {
            passwordStrengthBar.setStyle("-fx-accent: green;");
        }
    }

    // Fonction de validation du mot de passe
    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }
        return true;
    }

    @FXML
    private void onRegisterClicked() {
        if (validateFields()) {
            String password = passwordField.getText();

            // Vérifie si le mot de passe est fort
            if (!isPasswordStrong(password)) {
                showAlert("Erreur", "Le mot de passe doit être d'au moins 8 caractères, contenir des majuscules, des chiffres et des caractères spéciaux.", Alert.AlertType.ERROR);
                return;
            }

            if (!password.equals(confirmPasswordField.getText())) {
                showAlert("Erreur", "Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
                return;
            }

            try {
                User user = new User();
                user.setEmail(emailField.getText());
                user.setPassword(password);
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setAge(Integer.parseInt(ageField.getText()));
                user.setGenre(genreComboBox.getValue());
                user.setNumTel(Long.parseLong(numTelField.getText()));
                user.setRole("Parent");
                user.setSpecialite(specialiteField.getText());

                String verificationCode = userService.register(user);
                if (verificationCode != null) {
                    showAlert("Succès", "Un code de vérification a été envoyé à votre adresse email", Alert.AlertType.INFORMATION);
                    try {
                        MainApp.showVerifyEmail(user.getEmail());
                    } catch (Exception e) {
                        showAlert("Erreur", "Erreur lors de l'affichage de la page de vérification", Alert.AlertType.ERROR);
                        e.printStackTrace();
                    }
                } else {
                    showAlert("Erreur", "Erreur lors de l'inscription", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur est survenue lors de l'inscription", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onBackClicked() {
        try {
            MainApp.showChooseRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                emailField.getText().isEmpty() || numTelField.getText().isEmpty() ||
                ageField.getText().isEmpty() || genreComboBox.getValue() == null ||
                specialiteField.getText().isEmpty() ||
                passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return false;
        }

        if (!isValidEmail(emailField.getText())) {
            showAlert("Erreur", "Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        if (!isValidPhone(numTelField.getText())) {
            showAlert("Erreur", "Format de numéro de téléphone invalide", Alert.AlertType.ERROR);
            return false;
        }

        if (!isValidAge(ageField.getText())) {
            showAlert("Erreur", "L'âge doit être un nombre entre 28 et 99", Alert.AlertType.ERROR);
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
            return false;
        }

        try {
            if (userService.isEmailExists(emailField.getText())) {
                showAlert("Erreur", "Cette adresse email est déjà utilisée", Alert.AlertType.ERROR);
                return false;
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la vérification", Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{8}");  // Valide exactement 8 chiffres
    }

    private boolean isValidAge(String age) {
        try {
            int ageNum = Integer.parseInt(age);
            return ageNum >= 28 && ageNum <= 99;
        } catch (NumberFormatException e) {
            return false;
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

package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.novalearn.Entity.User;
import org.novalearn.MainApp;
import org.novalearn.services.quiz.UserService;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField numTelField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private TextField specialiteField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        genreComboBox.getItems().addAll("Homme", "Femme", "Autre");
        roleComboBox.getItems().addAll("ROLE_ELEVE", "ROLE_ENSEIGNANT", "ROLE_MEDECIN", "ROLE_ADMIN");

        // Par défaut, on cache le champ spécialité
        specialiteField.setVisible(false);
        specialiteField.setDisable(true);

        // Dès que l’on change de rôle…
        roleComboBox.valueProperty().addListener((obs, oldRole, newRole) -> {
            boolean isMedecin = "ROLE_MEDECIN".equals(newRole);
            // 1) afficher ou cacher le champ spécialité
            specialiteField.setVisible(isMedecin);
            specialiteField.setDisable(!isMedecin);
        });
    }

    @FXML
    private void onRegisterClicked() {
        if (validateFields()) {
            try {
                User user = new User();
                user.setEmail(emailField.getText());
                user.setPassword(passwordField.getText());
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setAge(Integer.parseInt(ageField.getText()));
                user.setGenre(genreComboBox.getValue());
                user.setNumTel(Long.parseLong(numTelField.getText()));
                user.setRole(roleComboBox.getValue());
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
            MainApp.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                emailField.getText().isEmpty() || numTelField.getText().isEmpty() ||
                ageField.getText().isEmpty() || genreComboBox.getValue() == null ||
                specialiteField.getText().isEmpty() || roleComboBox.getValue() == null ||
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
            showAlert("Erreur", "L'âge doit être un nombre entre 16 et 100", Alert.AlertType.ERROR);
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
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean isValidAge(String age) {
        try {
            int ageNum = Integer.parseInt(age);
            return ageNum >= 16 && ageNum <= 100;
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
package org.novalearn.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import org.novalearn.Entity.User;
import org.novalearn.MainApp;
import org.novalearn.services.quiz.UserService;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    @FXML private ListView<User> usersListView;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private TextField searchField;

    private final UserService userService;
    private ObservableList<User> usersList;
    private FilteredList<User> filteredUsers;

    public AdminDashboardController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        roleFilterComboBox.getItems().addAll("Tous", "Étudiant", "Enseignant", "Admin", "Médecin", "Parent");
        roleFilterComboBox.getSelectionModel().selectFirst();

        usersListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    Label infoLabel = new Label(String.format("%s %s (%s) - %s",
                            user.getNom(), user.getPrenom(), user.getEmail(), user.getRole()));
                    HBox.setHgrow(infoLabel, Priority.ALWAYS);

                    Button editButton = new Button("Modifier");
                    Button deleteButton = new Button("Supprimer");
                    Button activateDeactivateButton = new Button(user.getIsActive() ? "Désactiver" : "Activer");

                    editButton.setOnAction(e -> onEditUserClicked(user));
                    deleteButton.setOnAction(e -> onDeleteUserClicked(user));
                    activateDeactivateButton.setOnAction(e -> onActivateDeactivateUserClicked(user));

                    container.getChildren().addAll(infoLabel, editButton, deleteButton, activateDeactivateButton);
                    setGraphic(container);
                }
            }
        });

        loadUsers();

        roleFilterComboBox.setOnAction(e -> filterUsers());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers());
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            usersList = FXCollections.observableArrayList(users);
            filteredUsers = new FilteredList<>(usersList, p -> true);
            usersListView.setItems(filteredUsers);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les utilisateurs", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = roleFilterComboBox.getValue();

        filteredUsers.setPredicate(user -> {
            boolean matchesSearch = user.getNom().toLowerCase().contains(searchText)
                    || user.getPrenom().toLowerCase().contains(searchText)
                    || user.getEmail().toLowerCase().contains(searchText);

            boolean matchesRole = selectedRole.equals("Tous") || user.getRole().equalsIgnoreCase(selectedRole);

            return matchesSearch && matchesRole;
        });
    }

    @FXML
    private void onSearchClicked() {
        filterUsers();
    }

    @FXML
    private void onAddUserClicked() {
        try {
            MainApp.showAdminRegister();  // Ouvre la page pour ajouter un utilisateur
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire d'ajout", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void onEditUserClicked(User user) {
        if (user == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur à modifier", Alert.AlertType.ERROR);
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'utilisateur");
        dialog.setHeaderText("Modifier les informations de l'utilisateur");

        TextField emailField = new TextField(user.getEmail());
        TextField nomField = new TextField(user.getNom());
        TextField prenomField = new TextField(user.getPrenom());
        TextField ageField = new TextField(String.valueOf(user.getAge()));
        ComboBox<String> genreComboBox = new ComboBox<>(FXCollections.observableArrayList("Homme", "Femme", "Autre"));
        genreComboBox.setValue(user.getGenre());
        TextField numTelField = new TextField(String.valueOf(user.getNumTel()));
        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("Étudiant", "Enseignant", "Admin", "Médecin", "Parent"));
        roleComboBox.setValue(user.getRole());
        TextField specialiteField = new TextField(user.getSpecialite());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(new Label("Prénom:"), 0, 2);
        grid.add(prenomField, 1, 2);
        grid.add(new Label("Âge:"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Genre:"), 0, 4);
        grid.add(genreComboBox, 1, 4);
        grid.add(new Label("Téléphone:"), 0, 5);
        grid.add(numTelField, 1, 5);
        grid.add(new Label("Rôle:"), 0, 6);
        grid.add(roleComboBox, 1, 6);
        grid.add(new Label("Spécialité:"), 0, 7);
        grid.add(specialiteField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                user.setEmail(emailField.getText());
                user.setNom(nomField.getText());
                user.setPrenom(prenomField.getText());
                user.setAge(Integer.parseInt(ageField.getText()));
                user.setGenre(genreComboBox.getValue());
                user.setNumTel(Long.parseLong(numTelField.getText()));
                user.setRole(roleComboBox.getValue());
                user.setSpecialite(specialiteField.getText());
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            try {
                if (userService.updateUser(updatedUser)) {
                    showAlert("Succès", "Utilisateur modifié avec succès", Alert.AlertType.INFORMATION);
                    loadUsers();
                } else {
                    showAlert("Erreur", "Erreur lors de la modification de l'utilisateur", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification de l'utilisateur", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    private void onDeleteUserClicked(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (userService.deleteUser(user.getId())) {
                    usersList.remove(user);
                    showAlert("Succès", "Utilisateur supprimé avec succès", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Impossible de supprimer l'utilisateur", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void onActivateDeactivateUserClicked(User user) {
        try {
            boolean success = userService.toggleUserStatus(user.getId());

            if (success) {
                user.setIsActive(!user.getIsActive());
                usersListView.refresh();
                String status = user.getIsActive() ? "activé" : "désactivé";
                showAlert("Succès", "Utilisateur " + status + " avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de changer l'état de l'utilisateur", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'activation/désactivation", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onExportClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les utilisateurs en CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        fileChooser.setInitialFileName("utilisateurs.csv");

        Stage stage = (Stage) usersListView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = saveUsersToCSV(file);
            if (success) {
                showAlert("Succès", "Fichier exporté avec succès", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de l'exportation", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean saveUsersToCSV(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Email,Nom,Prénom,Âge,Genre,Téléphone,Rôle,Spécialité\n");
            for (User user : usersList) {
                writer.append(String.format("%s,%s,%s,%d,%s,%d,%s,%s\n",
                        user.getEmail(), user.getNom(), user.getPrenom(),
                        user.getAge(), user.getGenre(), user.getNumTel(),
                        user.getRole(), user.getSpecialite()));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void onLogoutClicked() {
        try {
            MainApp.showLogin();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la déconnexion", Alert.AlertType.ERROR);
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

    public void updateGraphData(double xValue, double yValue) {
    }
}

package org.novalearn.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    @FXML private Button btnCoursAdmin;
    @FXML private Button btnExerciceAdmin;
    @FXML private Button btnGenreAdmin;
    @FXML private Button btnReclamationAdmin;
    @FXML private Button btnBlogAdmin;
    @FXML private StackPane contentPane;
    @FXML private VBox sidebarContainer;

    private final UserService userService;
    private ObservableList<User> usersList;
    private FilteredList<User> filteredUsers;
    private User currentUser;

    public AdminDashboardController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        roleFilterComboBox.getItems().addAll("Tous", "ROLE_ELEVE", "ROLE_ENSEIGNANT", "ROLE_ADMIN", "ROLE_MEDECIN ", "ROLE_PARENT");
        roleFilterComboBox.getSelectionModel().selectFirst();

        usersListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(20);
                    Label infoLabel = new Label(String.format("%s %s (%s) - %s",
                            user.getNom(), user.getPrenom(), user.getEmail(), user.getRole()));
                    infoLabel.setStyle("-fx-font-size: 14px;");
                    HBox.setHgrow(infoLabel, Priority.ALWAYS);

                    MenuButton actionsButton = new MenuButton("Select Actions");
                    actionsButton.setStyle(
                            "-fx-background-color: #9370DB; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-background-radius: 20; " +
                                    "-fx-padding: 8 16;"
                    );

                    MenuItem editItem = new MenuItem("Modifier");
                    MenuItem deleteItem = new MenuItem("Supprimer");
                    String toggleText = user.getIsActive() ? "Désactiver" : "Activer";
                    MenuItem toggleItem = new MenuItem(toggleText);

                    editItem.setOnAction(e -> onEditUserClicked(user));
                    deleteItem.setOnAction(e -> onDeleteUserClicked(user));
                    toggleItem.setOnAction(e -> onActivateDeactivateUserClicked(user));

                    actionsButton.getItems().addAll(editItem, deleteItem, toggleItem);

                    VBox actionContainer = new VBox(actionsButton);
                    actionContainer.setPadding(new Insets(5));

                    container.getChildren().addAll(infoLabel, actionContainer);

                    setGraphic(container);
                }
            }
        });

        loadUsers();

        roleFilterComboBox.setOnAction(e -> filterUsers());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers());
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("🎯 AccueilController received user: "
                    + user.getEmail() + " (ID: " + user.getId() + ")");
            if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole())) {
                if (btnReclamationAdmin != null) {
                    btnReclamationAdmin.setVisible(false);
                    btnReclamationAdmin.setManaged(false);
                }
                if (btnCoursAdmin != null) {
                    btnCoursAdmin.setVisible(false);
                    btnCoursAdmin.setManaged(false);
                }
                if (btnExerciceAdmin != null) {
                    btnExerciceAdmin.setVisible(false);
                    btnExerciceAdmin.setManaged(false);
                }
                if (btnGenreAdmin != null) {
                    btnGenreAdmin.setVisible(false);
                    btnGenreAdmin.setManaged(false);
                }
                if (btnBlogAdmin != null) {
                    btnBlogAdmin.setVisible(false);
                    btnBlogAdmin.setManaged(false);
                }
            }
        } else {
            System.out.println("⚠️ AccueilController received null user!");
        }
    }

    @FXML
    private void showCoursAdmin() {
        loadModule0("cours_views/CoursExercieceDash.fxml");
    }

    @FXML
    private void showCoursExerciceAdmin() {
        loadModule0("exercice-views/exerciceAdmin.fxml");
    }

    @FXML
    private void SohwBlogAdmin() {
        loadModule0("blog-views/blog-admin.fxml");
    }

    private void loadModule0(String relativePath) {
        try {
            Pane module = FXMLLoader.load(
                    getClass().getResource("/org/novalearn/" + relativePath)
            );
            if (contentPane != null) {
                contentPane.getChildren().setAll(module);
            } else {
                System.err.println("⚠️ contentPane is null! Cannot load module: " + relativePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void onAddUserClicked() {
        try {
            MainApp.showAdminRegister();
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
        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("ROLE_ELEVE", "ROLE_ENSEIGNANT", "ROLE_ADMIN", "ROLE_MEDECIN ", "ROLE_PARENT"));
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
    private void loadModule(String relativePath) {
        try {
            Pane module = FXMLLoader.load(
                    getClass().getResource("/org/novalearn/" + relativePath)
            );
            contentPane.getChildren().setAll(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadModule1(String relativePath) {
        try {
            Pane module = FXMLLoader.load(
                    getClass().getResource("/org/novalearn/" + relativePath)
            );
            contentPane.getChildren().setAll(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

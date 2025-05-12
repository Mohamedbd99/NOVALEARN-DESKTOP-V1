package org.novalearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.novalearn.Entity.User;
import org.novalearn.MainApp;
import org.novalearn.controllers.Quiz.QuizzesController;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;


public class AccueilController {
    @FXML private StackPane contentPane;
    private ObjectMapper objectMapper;
    private User currentUser;
    @FXML private Button btnCoursAdmin;
    @FXML private Button btnExerciceAdmin;
    @FXML private Button btnGenreAdmin;
    @FXML private Button btnReclamationAdmin;
    @FXML private Button btnBlogAdmin;
    @FXML private Button btnAIcours;
    @FXML private Button btnQuiz;

    @FXML private VBox sideMenu;
    private boolean isMenuOpen = false;

    @FXML
    private void toggleMenu() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sideMenu);
        if (isMenuOpen) {
            transition.setToX(-200);  // cache vers la gauche
            transition.setOnFinished(e -> sideMenu.setVisible(false));
        } else {
            sideMenu.setVisible(true);
            transition.setToX(0);  // affiche sur l’écran
        }
        transition.play();
        isMenuOpen = !isMenuOpen;
    }
    @FXML
    public void initialize() {
        // we no longer know the user here; wait for setCurrentUser(...)
        this.objectMapper = new ObjectMapper();
        clearState();

    }

    private void clearState() {
        // any reset logic you need
    }

    @FXML
    private void showAccueilBody() {
        loadModule("acceauil-views/accueil_body.fxml");
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


    @FXML
    private void showQuizzes() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/novalearn/quizzes-views/quizzes.fxml")
            );
            Pane module = loader.load();

            // Pass the logged-in user to the quizzes screen
            QuizzesController qc = loader.getController();
            qc.setCurrentUser(currentUser);

            contentPane.getChildren().setAll(module);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAReclamation() {
        loadModule0("reclamation-views/reclamation.fxml");
    }
    @FXML
    private void showReclamationFront() {
        loadModule0("reclamation-views/ReclamationFront.fxml");
    }
    @FXML
    private void showGenre() {
        loadModule0("genre/Genre.fxml");
    }
    @FXML
    private void showCoursExercice() {
        loadModule0("cours_views/CoursFront.fxml");
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
    private void showReclamation() {
        loadModule0("reclamation-views/ReclamationFront.fxml");
    }
    @FXML
    private void showMessagerie() {
        loadModule0("Messagerie/Messagrie.fxml");
    }
    @FXML
    private void SohwBlogAdmin() {
        loadModule0("blog-views/blog-admin.fxml");
    }
    @FXML
    private void SohwBlogFront() {
        loadModule0("blog-views/blog-front.fxml");
    }
    @FXML
    private void SohwAIcours() {
        loadModule0("cours_views/aiCOURS.fxml");
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

    private void loadModule0(String relativePath) {
        try {
            Pane module = FXMLLoader.load(
                    getClass().getResource("/org/novalearn/" + relativePath)
            );
            contentPane.getChildren().setAll(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("🎯 AccueilController received user: "
                    + user.getEmail() + " (ID: " + user.getId() + ")");
            if (user != null && !"ROLE_ADMIN".equalsIgnoreCase(user.getRole())) {

                if (btnReclamationAdmin != null) {
                    btnReclamationAdmin.setVisible(false);
                    btnReclamationAdmin.setManaged(false);
                }
                if (btnAIcours != null) {
                    btnAIcours.setVisible(false);
                    btnAIcours.setManaged(false);
                }
            }


            if (user != null && !"ROLE_ENSEIGNANT".equalsIgnoreCase(user.getRole())) {
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

                if (btnAIcours != null) {
                    btnAIcours.setVisible(false);
                    btnAIcours.setManaged(false);
                }
                if (btnBlogAdmin != null) {
                    btnBlogAdmin.setVisible(false);
                    btnBlogAdmin.setManaged(false);
                }

            }
            if (user != null && !"ROLE_ELEVE".equalsIgnoreCase(user.getRole())) {
                if (btnQuiz != null) {
                    btnQuiz.setVisible(false);
                    btnQuiz.setManaged(false);
                }

            }

            if (user != null && !"ROLE_MEDECIN".equalsIgnoreCase(user.getRole())) {


            }

        } else {
            System.out.println("⚠️ AccueilController received null user!");
        }
    }
}


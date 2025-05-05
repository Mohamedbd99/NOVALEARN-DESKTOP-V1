package org.novalearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.novalearn.Entity.User;
import org.novalearn.controllers.Quiz.QuizzesController;

import java.io.IOException;

public class AccueilController {
    @FXML private StackPane contentPane;
    private ObjectMapper objectMapper;
    private User currentUser;

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
        loadModule0("reclamation-views/reclamation.fxml");
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
        } else {
            System.out.println("⚠️ AccueilController received null user!");
        }
    }
}

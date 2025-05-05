package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class AccueilController {
    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        // On startup, show the Accueil body (welcome panel)
        showAccueilBody();
    }

    @FXML
    private void showAccueilBody() {
        loadModule("acceauil-views/accueil_body.fxml");
    }

    @FXML
    private void showAReclamation() {
        loadModule("reclamation-views/reclamation.fxml");
    }
    @FXML
    private void showReclamationFront() {
        loadModule("reclamation-views/ReclamationFront.fxml");
    }
    @FXML
    private void showGenre() {
        loadModule("genre/Genre.fxml");
    }
    @FXML
    private void showCoursExercice() {
        loadModule("cours_views/CoursFront.fxml");
    }
    @FXML
    private void showCoursAdmin() {
        loadModule("cours_views/CoursExercieceDash.fxml");
    }
    @FXML
    private void showCoursExerciceAdmin() {
        loadModule("exercice-views/exerciceAdmin.fxml");
    }

    @FXML
    private void showReclamation() {
        loadModule("reclamation-views/reclamation.fxml");
    }
    @FXML
    private void showMessagerie() {
        loadModule("Messagerie/Messagrie.fxml");
    }
    @FXML
    private void showQuizzes() {
        loadModule("quizzes-views/quizzes.fxml");
    }
    @FXML
    private void SohwBlogAdmin() {
        loadModule("blog-views/blog-admin.fxml");
    }
    @FXML
    private void SohwBlogFront() {
        loadModule("blog-views/blog-front.fxml");
    }
    @FXML
    private void SohwAIcours() {
        loadModule("cours_views/aiCOURS.fxml");
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
}

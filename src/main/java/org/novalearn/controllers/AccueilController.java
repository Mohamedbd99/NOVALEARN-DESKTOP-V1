package org.novalearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.novalearn.Entity.User;
import org.novalearn.controllers.Quiz.QuizzesController;

public class AccueilController {
    @FXML private StackPane contentPane;
    private ObjectMapper objectMapper;

    @FXML
    public void initialize() {
               // we no longer know the user here; wait for setCurrentUser(...)
                       this.objectMapper = new ObjectMapper();
               clearState();
           }

    private void clearState() {
    }

    @FXML
    private void showAccueilBody() {
        loadModule("acceauil-views/accueil_body.fxml");
    }

    @FXML
    private void showCoursExercice() {
        loadModule("counrs_exercice-views/cours_exercice.fxml");
    }

    @FXML
    private void showReclamation() {
        loadModule("reclamation-views/reclamation.fxml");
    }

    @FXML
    private void showQuizzes() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/novalearn/quizzes-views/quizzes.fxml")
            );
            Pane module = loader.load();

            // **Pass the currentUser along:**
            QuizzesController qc = loader.getController();
            qc.setCurrentUser(currentUser);

            contentPane.getChildren().setAll(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private User currentUser;
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("🎯 AccueilController received user: " + user.getEmail() + " (ID: " + user.getId() + ")");
        } else {
            System.out.println("⚠️ AccueilController received null user!");
        }
    }
}

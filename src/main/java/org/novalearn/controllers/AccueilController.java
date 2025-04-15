package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class AccueilController {
    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        // load default module
        showCoursExercice();
    }

    @FXML
    private void showCoursExercice() {
        loadModule("cours_exercice.fxml");
    }

    @FXML
    private void showReclamation() {
        loadModule("reclamation.fxml");
    }

    @FXML
    private void showQuizzes() {
        loadModule("quizzes.fxml");
    }

    private void loadModule(String fxmlName) {
        try {
            Pane module = FXMLLoader.load(getClass().getResource("/org/novalearn/" + fxmlName));
            contentPane.getChildren().setAll(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

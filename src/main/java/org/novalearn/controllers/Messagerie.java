package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Messagerie {

    @FXML
    private void openClient() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/novalearn/client/client-view.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Client View");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openServer() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/novalearn/server/server-view.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Server View");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

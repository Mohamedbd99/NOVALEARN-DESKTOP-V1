package org.novalearn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void showAccueil() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/accueil.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Accueil");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

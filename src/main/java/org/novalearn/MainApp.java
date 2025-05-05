package org.novalearn;

import org.novalearn.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class MainApp extends Application {
    private static Stage primaryStage;
    private static Connection dbConnection;

    @Override
    public void init() throws Exception {
        // Called before start(); open your DB connection here
        dbConnection = DatabaseConnection.getConnection();
        if (dbConnection == null) {
            throw new IllegalStateException("Cannot start app: DB connection failed");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    @Override
    public void stop() throws Exception {
        // Called when the app is shutting down; close the connection
        if (dbConnection != null && !dbConnection.isClosed()) {
            dbConnection.close();
            System.out.println("DB connection closed.");
        }
    }

    public static Connection getDbConnection() {
        return dbConnection;
    }

    public static void showLogin() throws Exception {
      //  FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/login.fxml"));
        //FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/blog-views/blog-front.fxml"));
        //FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/cours_views/CoursExercieceDash.fxml"));
      FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/acceauil-views/accueil.fxml"));
        //FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/genre/Genre.fxml"));
        //FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/reclamation-views/reclamation.fxml"));
        //FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/Messagerie/Messagrie.fxml"));



        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void showAccueil() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/acceauil-views/accueil.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Accueil");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package org.novalearn;

import databaseConnection.DatabaseConnection;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.stripe.Stripe;           // ← add this

import java.sql.Connection;

public class MainApp extends Application {
    private static Stage primaryStage;
    private static Connection dbConnection;
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()   // optional
            .ignoreIfMissing()     // optional
            .load();

    @Override
    public void init() throws Exception {
        // 1) Load your Stripe secret key from the environment
        String stripeSecret = dotenv.get("STRIPE_SECRET_KEY");

        if (stripeSecret == null) {
            throw new IllegalStateException("Missing STRIPE_SECRET_KEY");
        }
        // 2) Initialize the Stripe SDK
        Stripe.apiKey = stripeSecret;

        // 3) Open your DB connection
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
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/login.fxml"));
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

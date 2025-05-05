package org.novalearn;


import org.novalearn.database.DatabaseConnection;

import databaseConnection.DatabaseConnection;
import io.github.cdimascio.dotenv.Dotenv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.novalearn.controllers.user.VerifyEmailController;

import com.stripe.Stripe;           // ← add this


import java.sql.Connection;
import java.net.URL;

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

    public static void showRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/register.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Inscription");
        primaryStage.show();
    }

    public static void showVerifyEmail(String email) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/verifyEmail.fxml"));
        Scene scene = new Scene(loader.load());
        VerifyEmailController controller = loader.getController();
        controller.setEmail(email);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Vérification de l'email");
        primaryStage.show();
    }

    public static void showAccueil() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/acceauil-views/accueil.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Accueil");
    }

    public static void showAdminDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/admin-views/admin-dashboard.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Dashboard Administrateur");
        primaryStage.show();
    }

    public static void showForgotPassword() throws Exception {
        try {
            String fxmlPath = "/org/novalearn/login-views/forgortPassword.fxml";
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Cannot find FXML file at: " + fxmlPath);
                throw new IllegalStateException("Cannot find forgot-password.fxml at " + fxmlPath);
            }
            System.out.println("Loading FXML from: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Mot de passe oublié");
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void showChooseRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/choose-register.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Choisir le type d'inscription");
        primaryStage.show();
    }

    public static void showStudentRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/student-register.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Inscription Étudiant");
        primaryStage.show();
    }

    public static void showParentRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/parent-register.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Inscription Parent");
        primaryStage.show();
    }

    public static void showAdminRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/novalearn/login-views/admin-register.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Ajouter un administrateur");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package org.novalearn.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Chaîne de connexion mise à jour
    private static final String URL =
            "jdbc:mysql://localhost:3306/novalearn"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";  // Mot de passe vide par défaut

    /**
     * Établit la connexion à la base de données.
     *
     * @return objet Connection si la connexion est établie, sinon null.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}

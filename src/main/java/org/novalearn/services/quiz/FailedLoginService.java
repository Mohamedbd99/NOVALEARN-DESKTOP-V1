package org.novalearn.services.quiz;

import org.novalearn.Entity.FailedLogin;

import java.sql.*;

public class FailedLoginService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/novalearn";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static final int MAX_FAILED_ATTEMPTS = 2;
    private static final int LOCK_TIME = 1 * 60 * 1000; // 1 minute en millisecondes


    // Vérifie si le compte est bloqué
    public boolean isAccountLocked(String email) throws SQLException {
        FailedLogin failedLogin = getFailedLoginByEmail(email);
        if (failedLogin != null && failedLogin.getBlockedUntil() != null) {
            Timestamp blockedUntil = failedLogin.getBlockedUntil();
            if (blockedUntil.after(new Timestamp(System.currentTimeMillis()))) {
                return true; // Le compte est bloqué
            }
        }
        return false;
    }

    // Enregistre une tentative échouée
    public void recordFailedAttempt(String email) throws SQLException {
        FailedLogin failedLogin = getFailedLoginByEmail(email);
        if (failedLogin != null) {
            // Si le nombre d'échecs dépasse la limite, on bloque l'utilisateur pendant un certain temps
            if (failedLogin.getAttempts() + 1 >= MAX_FAILED_ATTEMPTS) {
                lockAccount(email);
            } else {
                updateFailedLogin(failedLogin);
            }
        } else {
            // Si aucune tentative échouée, on crée une nouvelle entrée
            createFailedLogin(email);
        }
    }

    // Crée un enregistrement pour une tentative échouée
    private void createFailedLogin(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO failed_logins (email, attempts, last_attempt) VALUES (?, 1, NOW())";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }
        }
    }

    // Met à jour les tentatives échouées existantes
    private void updateFailedLogin(FailedLogin failedLogin) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "UPDATE failed_logins SET attempts = ?, last_attempt = NOW() WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, failedLogin.getAttempts() + 1);
                stmt.setString(2, failedLogin.getEmail());
                stmt.executeUpdate();
            }
        }
    }

    // Verrouille le compte après trop de tentatives échouées
    private void lockAccount(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "UPDATE failed_logins SET blocked_until = NOW() + INTERVAL ? SECOND WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, LOCK_TIME / 1000); // Convertir en secondes
                stmt.setString(2, email);
                stmt.executeUpdate();
            }
        }
    }

    // Récupère un enregistrement de tentative échouée par email
    private FailedLogin getFailedLoginByEmail(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT * FROM failed_logins WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new FailedLogin(
                            rs.getString("email"),
                            rs.getInt("attempts"),
                            rs.getTimestamp("last_attempt"),
                            rs.getTimestamp("blocked_until")
                    );
                }
            }
        }
        return null;
    }

    // Réinitialise les tentatives échouées après une connexion réussie
    public void resetFailedAttempts(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "UPDATE failed_logins SET attempts = 0, blocked_until = NULL WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }
        }
    }
}

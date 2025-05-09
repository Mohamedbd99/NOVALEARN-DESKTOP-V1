package org.novalearn.services.quiz;

import databaseConnection.DatabaseConnection;
import org.novalearn.Entity.User;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;




import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.Random;

public class UserService {
    private Connection cnx;

    public UserService() {
        cnx = DatabaseConnection.getConnection();

    }

    public User authenticate(String email, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ? AND password = ? AND isVerified = true";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setAge(rs.getInt("age"));
            user.setGenre(rs.getString("genre"));
            user.setNumTel(rs.getLong("num_tel"));
            user.setRole(rs.getString("role"));
            user.setSpecialite(rs.getString("specialite"));
            user.setIsVerified(rs.getBoolean("isVerified"));
            return user;
        }
        return null;
    }

    public String register(User user) throws SQLException {
        // Générer un code de vérification
        String verificationCode = generateVerificationCode();

        String query = "INSERT INTO user (email, password, nom, prenom, age, genre, num_tel, role, specialite, isVerified, verification_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getNom());
        ps.setString(4, user.getPrenom());
        ps.setInt(5, user.getAge());
        ps.setString(6, user.getGenre());
        ps.setLong(7, user.getNumTel());
        ps.setString(8, user.getRole());
        ps.setString(9, user.getSpecialite());
        ps.setBoolean(10, false); // L'utilisateur n'est pas vérifié par défaut
        ps.setString(11, verificationCode);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            // Envoyer l'email de vérification
            EmailService.sendVerificationEmail(user.getEmail(), verificationCode);
            return verificationCode;
        }
        return null;
    }

    public boolean verifyUser(String email, String verificationCode) throws SQLException {
        String query = "UPDATE user SET isVerified = true WHERE email = ? AND verification_code = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, verificationCode);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un code à 6 chiffres
        return String.valueOf(code);
    }

    public boolean isEmailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public boolean isPhoneExists(Long numTel) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE num_tel = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setLong(1, numTel);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        PreparedStatement ps = cnx.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setAge(rs.getInt("age"));
            user.setGenre(rs.getString("genre"));
            user.setNumTel(rs.getLong("num_tel"));
            user.setRole(rs.getString("role"));
            user.setSpecialite(rs.getString("specialite"));
            user.setIsVerified(rs.getBoolean("isVerified"));
            user.setIsActive(rs.getBoolean("is_active"));
            users.add(user);
        }
        return users;
    }

    public boolean deleteUser(Long id) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setLong(1, id);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE user SET email = ?, password = ?, nom = ?, prenom = ?, " +
                "age = ?, genre = ?, num_tel = ?, role = ?, specialite = ?, isVerified = ? " +
                "WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getNom());
        ps.setString(4, user.getPrenom());
        ps.setInt(5, user.getAge());
        ps.setString(6, user.getGenre());
        ps.setLong(7, user.getNumTel());
        ps.setString(8, user.getRole());
        ps.setString(9, user.getSpecialite());
        ps.setBoolean(10, user.getIsVerified());
        ps.setLong(11, user.getId());

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    public User getUserById(Long id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setAge(rs.getInt("age"));
            user.setGenre(rs.getString("genre"));
            user.setNumTel(rs.getLong("num_tel"));
            user.setRole(rs.getString("role"));
            user.setSpecialite(rs.getString("specialite"));
            user.setIsVerified(rs.getBoolean("isVerified"));
            return user;
        }
        return null;
    }

    public boolean updateUserVerification(Long id, boolean isVerified) throws SQLException {
        String query = "UPDATE user SET isVerified = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setBoolean(1, isVerified);
        ps.setLong(2, id);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur vérification email : " + e.getMessage());
        }
        return false;
    }
    public String resetPassword(String email) {
        if (!emailExists(email)) {
            return null;
        }

        String newPassword = generateRandomPassword();
        String sql = "UPDATE user SET password = ? WHERE email = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, newPassword); // tu peux hash ici
            pst.setString(2, email);
            pst.executeUpdate();

            // Envoyer le mot de passe par mail
            String message = "Bonjour,\n\nVotre nouveau mot de passe est : " + newPassword +
                    "\n\nMerci de le changer après connexion.";
            EmailService.sendEmail(email, "Réinitialisation de votre mot de passe", message);

            return newPassword;
        } catch (SQLException e) {
            System.out.println("Erreur reset password: " + e.getMessage());
        }

        return null;
    }


    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randIndex = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(randIndex));
        }
        return sb.toString();
    }

    private String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, hashed);
            return number.toString(16); // Convertir en hexadécimal
        } catch (Exception e) {
            throw new RuntimeException("Erreur de hachage : " + e.getMessage());
        }
    }

    public boolean toggleUserStatus(Long userId) throws SQLException {
        String query = "UPDATE user SET is_active = NOT is_active WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, userId);  // Utilisation de setLong() au lieu de setInt()
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }



}
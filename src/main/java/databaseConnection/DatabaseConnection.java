package databaseConnection;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Load .env at startup; fails if .env is missing
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()   // optional
            .ignoreIfMissing()     // optional
            .load();

    // Read values from .env (no fallbacks)
    private static final String URL      = dotenv.get("DB_URL");
    private static final String USER     = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASS");

    static {
        // sanity check
        if (URL == null || USER == null || PASSWORD == null) {
            System.err.println("❌ Missing one or more DB_* variables in .env");
            System.err.println("   DB_URL=" + URL);
            System.err.println("   DB_USER=" + USER);
            System.err.println("   DB_PASS=" + PASSWORD);
            throw new IllegalStateException("Database credentials not set in .env");
        }
    }

    /**
     * Établit la connexion à la base de données.
     *
     * @return objet Connection si la connexion est établie, sinon null.
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie avec succès.$$");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

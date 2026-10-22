package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/java_shop?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";

    private static String cachedPassword = null;

    /**
     * Obtient une connexion active à la base de données.
     * Tente successivement avec mot de passe vide ("") puis avec "root" pour une compatibilité
     * maximale entre XAMPP/WAMP et les conteneurs Docker/installations MySQL par défaut.
     */
    public static Connection getConnection() throws SQLException {
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASS");

        String url = (envUrl != null && !envUrl.isEmpty()) ? envUrl : DEFAULT_URL;
        String user = (envUser != null && !envUser.isEmpty()) ? envUser : DEFAULT_USER;

        if (envPass != null) {
            return DriverManager.getConnection(url, user, envPass);
        }

        if (cachedPassword != null) {
            return DriverManager.getConnection(url, user, cachedPassword);
        }

        // Essayer d'abord avec mot de passe vide (XAMPP / WAMP)
        try {
            Connection conn = DriverManager.getConnection(url, user, "");
            cachedPassword = "";
            return conn;
        } catch (SQLException ex1) {
            // Repli sur "root" (Docker / MySQL Server avec mot de passe root)
            try {
                Connection conn = DriverManager.getConnection(url, user, "root");
                cachedPassword = "root";
                return conn;
            } catch (SQLException ex2) {
                throw new SQLException("Impossible de se connecter à la base de données MySQL sur " + url +
                        ". Erreur avec mot de passe vide: " + ex1.getMessage() +
                        " | Erreur avec mot de passe 'root': " + ex2.getMessage(), ex2);
            }
        }
    }

    /**
     * Initialise les tables requises dans la base de données si elles n'existent pas déjà.
     */
    public static void initDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nom VARCHAR(100) NOT NULL,
                email VARCHAR(150) NOT NULL UNIQUE,
                mdp VARCHAR(255) NOT NULL,
                age INT
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;

        String createProjectsTable = """
            CREATE TABLE IF NOT EXISTS projects (
                id INT AUTO_INCREMENT PRIMARY KEY,
                titre VARCHAR(150) NOT NULL,
                description TEXT,
                date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                user_id INT NOT NULL,
                CONSTRAINT fk_project_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;

        String createTasksTable = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INT AUTO_INCREMENT PRIMARY KEY,
                titre VARCHAR(150) NOT NULL,
                description TEXT,
                statut VARCHAR(30) DEFAULT 'A_FAIRE',
                date_echeance DATE,
                project_id INT NOT NULL,
                CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createProjectsTable);
            stmt.executeUpdate(createTasksTable);
        } catch (SQLException e) {
            System.err.println("Avertissement: Erreur lors de l'initialisation des tables : " + e.getMessage());
        }
    }
}

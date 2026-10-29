package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/java_shop?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static String cachedPassword = null;

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

        try {
            Connection conn = DriverManager.getConnection(url, user, "");
            cachedPassword = "";
            return conn;
        } catch (SQLException ex1) {
            try {
                Connection conn = DriverManager.getConnection(url, user, "root");
                cachedPassword = "root";
                return conn;
            } catch (SQLException ex2) {
                throw new SQLException("Impossible de se connecter à MySQL : " + ex2.getMessage(), ex2);
            }
        }
    }
}

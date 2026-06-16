package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String uri = "jdbc:mysql://localhost:3306/java_shop";
    private static final String user = "root";
    private static final String password = "";

    public  static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(uri, user, password);
    }
}

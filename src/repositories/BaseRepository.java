package repositories;

import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRepository<T> {

    protected Connection conn;
    protected String tableName;

    protected BaseRepository() throws SQLException {
        this.conn = Database.getConnection();
    }

    protected Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = Database.getConnection();
        }
        return conn;
    }

    protected abstract T mapToEntity(ResultSet resultSet) throws SQLException;

    public List<T> getAll() throws SQLException {
        String query = "SELECT * FROM " + tableName;
        List<T> list = new ArrayList<>();

        try (PreparedStatement statement = getConnection().prepareStatement(query);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                list.add(mapToEntity(results));
            }
        }
        return list;
    }

    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM " + tableName + " WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public T findById(int id) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapToEntity(results);
                }
            }
        }
        return null;
    }
}

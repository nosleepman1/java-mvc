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
        conn = Database.getConnection();
    }


    protected abstract T mapToEntity(ResultSet resultSet) throws SQLException;

    public List<T> getAll() throws SQLException {
        String query = "SELECT * FROM " + tableName;
        List<T> list = new ArrayList<>();

        try(PreparedStatement statement = conn.prepareStatement(query);
            ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                list.add(mapToEntity(results));
            }
        }
        return list;

    }

}

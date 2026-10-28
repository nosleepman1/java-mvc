package repositories;

import Entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository extends BaseRepository<User> {

    public UserRepository() throws SQLException {
        super();
        this.tableName = "users";
    }

    @Override
    protected User mapToEntity(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setNom(resultSet.getString("nom"));
        user.setEmail(resultSet.getString("email"));
        user.setMdp(resultSet.getString("mdp"));
        user.setAge(resultSet.getInt("age"));
        return user;
    }

    public boolean create(User user) throws SQLException {
        String sql = "INSERT INTO users (nom, email, mdp, age) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getMdp());
            statement.setInt(4, user.getAge());

            return statement.executeUpdate() == 1;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEntity(resultSet);
                }
            }
        }

        return null;
    }

    public boolean update(int id, User user) throws SQLException {
        String sql = "UPDATE users SET nom = ?, email = ?, mdp = ?, age = ? WHERE id = ?";

        // verify if user exist
        User existingUser = findById(id);
        if (existingUser == null) {
            return false;
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getMdp());
            statement.setInt(4, user.getAge());
            statement.setInt(5, user.getId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND mdp = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }

        return false;
    }

    

    
}

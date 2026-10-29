package repositories;

import Entities.User;
import utils.PasswordHasher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        try (PreparedStatement statement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getMdp());
            statement.setInt(4, user.getAge());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
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
        String sql = "UPDATE users SET nom = ?, email = ?, age = ? WHERE id = ?";

        User existingUser = findById(id);
        if (existingUser == null) {
            return false;
        }

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getAge());
            statement.setInt(4, id);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(int id, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET mdp = ? WHERE id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, newHashedPassword);
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        }
    }

    public User authenticate(String email, String hashedPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?) AND mdp = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, hashedPassword);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEntity(resultSet);
                }
            }
        }
        return null;
    }
}

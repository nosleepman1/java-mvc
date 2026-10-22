package Controllers;

import Entities.User;
import repositories.UserRepository;
import utils.PasswordHasher;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    private final UserRepository userRepository;

    public UserController() throws SQLException {
        this.userRepository = new UserRepository();
    }

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() throws SQLException {
        return userRepository.getAll();
    }

    public User getUserById(int id) throws SQLException {
        return userRepository.findById(id);
    }

    public boolean createUser(String nom, String email, String password, int age) throws SQLException {
        String hashedPassword = PasswordHasher.hashPassword(password);
        User user = new User(nom, email, hashedPassword, age);
        return userRepository.create(user);
    }

    public boolean updateUser(int id, User user) throws SQLException {
        return userRepository.update(id, user);
    }

    public boolean deleteUser(int id) throws SQLException {
        return userRepository.delete(id);
    }
}

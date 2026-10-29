package Controllers;

import Entities.User;
import repositories.UserRepository;
import java.sql.SQLException;

public class AuthController {

    private final UserRepository userRepository;
    private User currentUser;

    public AuthController() throws SQLException {
        this.userRepository = new UserRepository();
    }

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

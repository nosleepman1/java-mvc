package Controllers;

import Entities.User;
import repositories.UserRepository;
import utils.PasswordHasher;
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

    public boolean register(String nom, String email, String rawPassword, int age) throws SQLException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'adresse email est invalide.");
        }
        if (rawPassword == null || rawPassword.trim().length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit comporter au moins 4 caractères.");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("L'âge saisi est invalide.");
        }

        if (userRepository.existsByEmail(email.trim())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cette adresse email.");
        }

        String hashedPassword = PasswordHasher.hashPassword(rawPassword);
        User newUser = new User(nom.trim(), email.trim(), hashedPassword, age);

        return userRepository.create(newUser);
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

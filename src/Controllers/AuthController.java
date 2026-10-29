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

    /**
     * Tente de connecter l'utilisateur avec son email et son mot de passe en clair.
     * Le mot de passe est automatiquement haché en SHA-256 avant la comparaison en base.
     */
    public User login(String email, String rawPassword) throws SQLException {
        if (email == null || email.trim().isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return null;
        }

        String hashedPassword = PasswordHasher.hashPassword(rawPassword);
        User user = userRepository.authenticate(email.trim(), hashedPassword);

        if (user != null) {
            this.currentUser = user;
            return user;
        }

        // Cas de secours pour d'anciens utilisateurs non encore hachés
        User legacyUser = userRepository.findByEmail(email.trim());
        if (legacyUser != null && legacyUser.getMdp().equals(rawPassword)) {
            // Mettre à jour automatiquement le mot de passe vers le format haché
            userRepository.updatePassword(legacyUser.getId(), hashedPassword);
            legacyUser.setMdp(hashedPassword);
            this.currentUser = legacyUser;
            return legacyUser;
        }

        return null;
    }

    /**
     * Enregistre un nouvel utilisateur avec un mot de passe sécurisé et haché.
     */
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

    public boolean updateProfile(String nom, int age) throws SQLException {
        if (!isLoggedIn()) {
            return false;
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("L'âge saisi est invalide.");
        }

        currentUser.setNom(nom.trim());
        currentUser.setAge(age);
        return userRepository.update(currentUser.getId(), currentUser);
    }

    public boolean changePassword(String oldRawPassword, String newRawPassword) throws SQLException {
        if (!isLoggedIn()) {
            return false;
        }
        if (!PasswordHasher.verifyPassword(oldRawPassword, currentUser.getMdp())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
        }
        if (newRawPassword == null || newRawPassword.trim().length() < 4) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit comporter au moins 4 caractères.");
        }

        String newHashedPassword = PasswordHasher.hashPassword(newRawPassword);
        boolean updated = userRepository.updatePassword(currentUser.getId(), newHashedPassword);
        if (updated) {
            currentUser.setMdp(newHashedPassword);
        }
        return updated;
    }
}


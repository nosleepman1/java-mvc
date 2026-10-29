package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitaire pour le hachage sécurisé des mots de passe en utilisant SHA-256.
 */
public class PasswordHasher {

    /**
     * Hache un mot de passe en clair avec l'algorithme SHA-256.
     *
     * @param password Le mot de passe en clair
     * @return L'empreinte hexadécimale (64 caractères) du mot de passe haché
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur critique: algorithme SHA-256 introuvable", e);
        }
    }

    /**
     * Vérifie si un mot de passe en clair correspond au mot de passe haché stocké.
     *
     * @param rawPassword Le mot de passe saisi en clair
     * @param hashedPassword Le mot de passe haché enregistré en base
     * @return true si les mots de passe correspondent, false sinon
     */
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        String hashedInput = hashPassword(rawPassword);
        return hashedInput.equalsIgnoreCase(hashedPassword);
    }
}


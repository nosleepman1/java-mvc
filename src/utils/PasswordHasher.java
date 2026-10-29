package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return new String(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur critique: algorithme SHA-256 introuvable", e);
        }
    }
}

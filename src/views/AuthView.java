package views;

import Controllers.AuthController;
import Entities.User;

import java.sql.SQLException;

public class AuthView {

    private final AuthController authController;

    public AuthView(AuthController authController) {
        this.authController = authController;
    }

    /**
     * Affiche le menu d'authentification principal pour les utilisateurs non connectés.
     * @return true si l'application doit continuer, false pour quitter.
     */
    public boolean showAuthMenu() {
        ConsoleUtils.printHeader("BIENVENUE - GESTIONNAIRE DE PROJETS & TÂCHES");
        System.out.println(" 1. Se connecter");
        System.out.println(" 2. S'inscrire (Nouveau compte avec mot de passe haché)");
        System.out.println(" 0. Quitter l'application");
        System.out.println("---------------------------------------------------------------");

        int choice = ConsoleUtils.readIntBetween("Votre choix", 0, 2);

        switch (choice) {
            case 1 -> renderLogin();
            case 2 -> renderRegister();
            case 0 -> {
                ConsoleUtils.printInfo("Au revoir et à bientôt !");
                return false;
            }
        }
        return true;
    }

    private void renderLogin() {
        ConsoleUtils.printSubHeader("CONNEXION");
        String email = ConsoleUtils.readNonEmptyString("Adresse Email");
        String password = ConsoleUtils.readPassword("Mot de passe");

        try {
            User user = authController.login(email, password);
            if (user != null) {
                ConsoleUtils.printSuccess("Connexion réussie ! Bienvenue " + user.getNom() + ".");
            } else {
                ConsoleUtils.printError("Identifiants incorrects (email ou mot de passe invalide).");
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur de base de données lors de la connexion : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void renderRegister() {
        ConsoleUtils.printSubHeader("INSCRIPTION (SÉCURISÉE AVEC HACHAGE SHA-256)");
        String nom = ConsoleUtils.readNonEmptyString("Votre nom complet");
        String email = ConsoleUtils.readNonEmptyString("Votre adresse email");
        String password = ConsoleUtils.readPassword("Votre mot de passe (min. 4 caractères)");
        int age = ConsoleUtils.readInt("Votre âge");

        try {
            boolean created = authController.register(nom, email, password, age);
            if (created) {
                ConsoleUtils.printSuccess("Compte créé avec succès ! Votre mot de passe a été haché en SHA-256.");
                ConsoleUtils.printInfo("Vous pouvez maintenant vous connecter.");
            } else {
                ConsoleUtils.printError("Impossible de créer le compte.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL lors de l'inscription : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }
}


package views;

import Controllers.AuthController;
import Controllers.ProjectController;
import Entities.User;

import java.sql.SQLException;

public class MainView {

    private final AuthController authController;
    private final AuthView authView;
    private final ProjectView projectView;
    private final TaskView taskView;
    private final ProjectController projectController;

    public MainView(AuthController authController,
                    AuthView authView,
                    ProjectView projectView,
                    TaskView taskView,
                    ProjectController projectController) {
        this.authController = authController;
        this.authView = authView;
        this.projectView = projectView;
        this.taskView = taskView;
        this.projectController = projectController;
    }

    /**
     * Démarre la boucle principale de l'application.
     */
    public void start() {
        boolean running = true;
        while (running) {
            if (!authController.isLoggedIn()) {
                running = authView.showAuthMenu();
            } else {
                showDashboard();
            }
        }
    }

    private void showDashboard() {
        User currentUser = authController.getCurrentUser();
        int projectCount = 0;
        try {
            projectCount = projectController.countProjects(currentUser.getId());
        } catch (SQLException ignored) {}

        ConsoleUtils.printHeader("TABLEAU DE BORD - GESTIONNAIRE DE TÂCHES & PROJETS");
        System.out.println(" Connecté en tant que : " + currentUser.getNom() + " (" + currentUser.getEmail() + ")");
        System.out.println(" Projets enregistrés  : " + projectCount);
        System.out.println("---------------------------------------------------------------");
        System.out.println(" 1. Gestion des Projets (Créer, lister, voir tâches & avancement)");
        System.out.println(" 2. Gestion des Tâches (Ajouter, changer statut, échéances)");
        System.out.println(" 3. Mon Profil & Sécurité (Changer le mot de passe)");
        System.out.println(" 4. Se déconnecter");
        System.out.println(" 0. Quitter l'application");
        System.out.println("---------------------------------------------------------------");

        int choice = ConsoleUtils.readIntBetween("Votre choix", 0, 4);

        switch (choice) {
            case 1 -> projectView.showProjectMenu(currentUser);
            case 2 -> taskView.showTaskMenu(currentUser);
            case 3 -> showProfileMenu(currentUser);
            case 4 -> {
                authController.logout();
                ConsoleUtils.printSuccess("Vous avez été déconnecté avec succès.");
                ConsoleUtils.pause();
            }
            case 0 -> {
                authController.logout();
                ConsoleUtils.printInfo("Au revoir et à bientôt !");
                System.exit(0);
            }
        }
    }

    private void showProfileMenu(User currentUser) {
        boolean inProfile = true;
        while (inProfile) {
            ConsoleUtils.printSubHeader("MON PROFIL & SÉCURITÉ");
            System.out.println(" Nom   : " + currentUser.getNom());
            System.out.println(" Email : " + currentUser.getEmail());
            System.out.println(" Âge   : " + currentUser.getAge() + " ans");
            System.out.println("---------------------------------------------------------------");
            System.out.println(" 1. Modifier mes informations (Nom, Âge)");
            System.out.println(" 2. Modifier mon mot de passe (Hachage SHA-256)");
            System.out.println(" 0. Retour au tableau de bord");
            System.out.println("---------------------------------------------------------------");

            int choice = ConsoleUtils.readIntBetween("Votre choix", 0, 2);

            switch (choice) {
                case 1 -> {
                    String newNom = ConsoleUtils.readNonEmptyString("Nouveau nom");
                    int newAge = ConsoleUtils.readInt("Nouvel âge");
                    try {
                        boolean updated = authController.updateProfile(newNom, newAge);
                        if (updated) {
                            ConsoleUtils.printSuccess("Profil mis à jour !");
                        } else {
                            ConsoleUtils.printError("Échec de la mise à jour.");
                        }
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                    ConsoleUtils.pause();
                }
                case 2 -> {
                    String oldPass = ConsoleUtils.readPassword("Ancien mot de passe");
                    String newPass = ConsoleUtils.readPassword("Nouveau mot de passe");
                    try {
                        boolean changed = authController.changePassword(oldPass, newPass);
                        if (changed) {
                            ConsoleUtils.printSuccess("Mot de passe modifié et sécurisé en SHA-256 avec succès !");
                        } else {
                            ConsoleUtils.printError("Échec du changement de mot de passe.");
                        }
                    } catch (Exception e) {
                        ConsoleUtils.printError(e.getMessage());
                    }
                    ConsoleUtils.pause();
                }
                case 0 -> inProfile = false;
            }
        }
    }
}


import Controllers.AuthController;
import Controllers.ProjectController;
import Controllers.TaskController;
import Entities.Project;
import Entities.Task;
import Entities.TaskStatus;
import Entities.User;
import database.Database;
import utils.PasswordHasher;

import java.time.LocalDate;
import java.util.List;

public class TestApp {

    public static void main(String[] args) {
        System.out.println("=== Début du test d'intégration ===");

        // 1. Initialisation DB
        Database.initDatabase();
        System.out.println("Base de données initialisée.");

        try {
            AuthController authController = new AuthController();
            ProjectController projectController = new ProjectController();
            TaskController taskController = new TaskController();

            // Test 2. Hachage mot de passe
            String rawPass = "Secret123!";
            String hashed = PasswordHasher.hashPassword(rawPass);
            System.out.println("Mot de passe en clair: " + rawPass);
            System.out.println("Mot de passe haché (SHA-256): " + hashed);
            assert hashed.length() == 64 : "Le hachage SHA-256 doit comporter 64 caractères";
            assert PasswordHasher.verifyPassword(rawPass, hashed) : "La vérification du mot de passe doit réussir";

            // Test 3. Inscription utilisateur
            String testEmail = "test_" + System.currentTimeMillis() + "@univ.sn";
            boolean regOk = authController.register("Aissatou Diallo", testEmail, rawPass, 22);
            System.out.println("Inscription réussie ? " + regOk);
            assert regOk : "L'inscription doit réussir";

            // Test 4. Connexion avec mot de passe en clair (le controller doit hacher et comparer)
            User user = authController.login(testEmail, rawPass);
            System.out.println("Connexion réussie ? " + (user != null));
            assert user != null : "L'utilisateur doit pouvoir se connecter";
            assert user.getEmail().equalsIgnoreCase(testEmail) : "L'email doit correspondre";
            assert user.getMdp().equals(hashed) : "Le mot de passe en base DOIT être le hash SHA-256";
            System.out.println("Vérification en base : mot de passe bien stocké sous forme hachée !");

            // Test 5. Échec de connexion avec mauvais mot de passe
            User failUser = authController.login(testEmail, "MauvaisMotDePasse");
            assert failUser == null : "La connexion avec mauvais mot de passe doit échouer";
            System.out.println("Connexion avec mauvais mot de passe correctement rejetée.");

            // Test 6. Création d'un projet
            boolean projOk = projectController.createProject("Projet Java MVC", "Application complète de gestion de tâches", user.getId());
            assert projOk : "La création du projet doit réussir";
            System.out.println("Projet créé avec succès !");

            List<Project> projects = projectController.getProjectsByUser(user.getId());
            assert !projects.isEmpty() : "La liste des projets ne doit pas être vide";
            Project createdProject = projects.get(0);
            System.out.println("Projet récupéré : " + createdProject);

            // Test 7. Ajout de tâches au projet
            boolean task1Ok = taskController.createTask(
                    createdProject.getId(),
                    "Concevoir la couche MVC",
                    "Définir Entities, Repositories, Controllers et Views",
                    TaskStatus.TERMINE.name(),
                    LocalDate.now().plusDays(2),
                    user.getId()
            );
            assert task1Ok : "La création de la tâche 1 doit réussir";

            boolean task2Ok = taskController.createTask(
                    createdProject.getId(),
                    "Hachage SHA-256 des mots de passe",
                    "Sécuriser l'authentification avec PasswordHasher",
                    TaskStatus.EN_COURS.name(),
                    LocalDate.now().plusDays(5),
                    user.getId()
            );
            assert task2Ok : "La création de la tâche 2 doit réussir";

            // Test 8. Récupération du projet avec ses tâches
            Project projectWithTasks = projectController.getProjectWithTasks(createdProject.getId(), user.getId());
            System.out.println("Nombre de tâches dans le projet : " + projectWithTasks.getTasks().size());
            assert projectWithTasks.getTasks().size() == 2 : "Le projet doit contenir 2 tâches";
            System.out.println("Avancement du projet : " + projectWithTasks.getProgressPercentage() + "%");

            // Test 9. Changement de statut d'une tâche
            Task task2 = projectWithTasks.getTasks().get(1);
            boolean statusUpdated = taskController.changeTaskStatus(task2.getId(), TaskStatus.TERMINE.name(), user.getId());
            assert statusUpdated : "Le changement de statut doit réussir";
            System.out.println("Statut de la tâche 2 mis à jour vers TERMINE.");

            Project updatedProj = projectController.getProjectWithTasks(createdProject.getId(), user.getId());
            System.out.println("Nouvel avancement du projet : " + updatedProj.getProgressPercentage() + "%");
            assert updatedProj.getProgressPercentage() == 100.0 : "L'avancement doit être de 100%";

            // Test 10. Déconnexion
            authController.logout();
            assert !authController.isLoggedIn() : "L'utilisateur doit être déconnecté";
            System.out.println("Déconnexion réussie.");

            System.out.println("\n>>> TOUS LES TESTS D'INTÉGRATION ONT RÉUSSI AVEC SUCCÈS ! <<<");

        } catch (Exception e) {
            System.err.println("ÉCHEC DU TEST : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}


import Controllers.AuthController;
import Controllers.ProjectController;
import Controllers.TaskController;
import database.Database;
import views.AuthView;
import views.MainView;
import views.ProjectView;
import views.TaskView;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Initialisation du gestionnaire de tâches et de projets...");

        // 1. Initialisation de la base de données et des tables (users, projects, tasks)
        Database.initDatabase();

        try {
            // 2. Instanciation des contrôleurs (MVC - Contrôleurs)
            AuthController authController = new AuthController();
            ProjectController projectController = new ProjectController();
            TaskController taskController = new TaskController();

            // 3. Instanciation des vues avec injection des contrôleurs (MVC - Vues)
            AuthView authView = new AuthView(authController);
            ProjectView projectView = new ProjectView(projectController, taskController);
            TaskView taskView = new TaskView(taskController, projectController);
            MainView mainView = new MainView(authController, authView, projectView, taskView, projectController);

            // 4. Lancement du menu principal
            mainView.start();

        } catch (SQLException e) {
            System.err.println("Erreur critique lors de l'initialisation de l'application : " + e.getMessage());
            e.printStackTrace();
        }
    }
}


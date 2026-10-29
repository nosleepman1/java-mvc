package views;

import Controllers.ProjectController;
import Controllers.TaskController;
import Entities.Task;
import Entities.User;
import java.sql.SQLException;
import java.util.List;

public class TaskView {

    private final TaskController taskController;
    private final ProjectController projectController;

    public TaskView(TaskController taskController, ProjectController projectController) {
        this.taskController = taskController;
        this.projectController = projectController;
    }

    public void renderListAllTasks(User currentUser) {
        ConsoleUtils.printSubHeader("TOUTES MES TÂCHES");
        try {
            List<Task> tasks = taskController.getAllTasksByUser(currentUser.getId());
            if (tasks.isEmpty()) {
                ConsoleUtils.printInfo("Aucune tâche trouvée.");
            } else {
                for (Task t : tasks) {
                    System.out.println(t);
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }
}

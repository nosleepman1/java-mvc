package views;

import Controllers.ProjectController;
import Controllers.TaskController;
import Entities.Project;
import Entities.User;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProjectView {

    private final ProjectController projectController;
    private final TaskController taskController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProjectView(ProjectController projectController, TaskController taskController) {
        this.projectController = projectController;
        this.taskController = taskController;
    }

    public void renderListProjects(User currentUser) {
        ConsoleUtils.printSubHeader("MES PROJETS");
        try {
            List<Project> projects = projectController.getProjectsByUser(currentUser.getId());
            if (projects.isEmpty()) {
                ConsoleUtils.printInfo("Vous n'avez aucun projet pour le moment.");
            } else {
                for (Project p : projects) {
                    System.out.println(String.format("• [#%d] %s", p.getId(), p.getTitre()));
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur lors de la récupération des projets : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }
}

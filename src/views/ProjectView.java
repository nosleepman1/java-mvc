package views;

import Controllers.ProjectController;
import Controllers.TaskController;
import Entities.Project;
import Entities.Task;
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

    public void showProjectMenu(User currentUser) {
        boolean inProjectMenu = true;
        while (inProjectMenu) {
            ConsoleUtils.printHeader("GESTION DES PROJETS - " + currentUser.getNom());
            System.out.println(" 1. Liste de mes projets");
            System.out.println(" 2. Créer un nouveau projet");
            System.out.println(" 3. Consulter un projet en détail (avec ses tâches)");
            System.out.println(" 4. Modifier un projet");
            System.out.println(" 5. Supprimer un projet");
            System.out.println(" 0. Retour au tableau de bord");
            System.out.println("---------------------------------------------------------------");

            int choice = ConsoleUtils.readIntBetween("Votre choix", 0, 5);

            switch (choice) {
                case 1 -> renderListProjects(currentUser);
                case 2 -> renderCreateProject(currentUser);
                case 3 -> renderProjectDetails(currentUser);
                case 4 -> renderUpdateProject(currentUser);
                case 5 -> renderDeleteProject(currentUser);
                case 0 -> inProjectMenu = false;
            }
        }
    }

    public void renderListProjects(User currentUser) {
        ConsoleUtils.printSubHeader("MES PROJETS");
        try {
            List<Project> projects = projectController.getProjectsByUser(currentUser.getId());
            if (projects.isEmpty()) {
                ConsoleUtils.printInfo("Vous n'avez aucun projet pour le moment. Créez-en un via le menu 2 !");
            } else {
                for (Project p : projects) {
                    long totalTasks = p.getTasks().size();
                    long doneTasks = p.getCompletedTasksCount();
                    double pct = p.getProgressPercentage();
                    String bar = ConsoleUtils.formatProgressBar(pct, 12);

                    System.out.println(String.format("• [#%d] %s", p.getId(), p.getTitre()));
                    if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                        System.out.println("   Description : " + p.getDescription());
                    }
                    System.out.println(String.format("   Avancement  : %s (%d/%d tâche(s) terminée(s))",
                            bar, doneTasks, totalTasks));
                    System.out.println("   Créé le     : " +
                            (p.getDateCreation() != null ? p.getDateCreation().format(DATE_FORMATTER) : "-"));
                    System.out.println("   --------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur lors de la récupération des projets : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderCreateProject(User currentUser) {
        ConsoleUtils.printSubHeader("NOUVEAU PROJET");
        String titre = ConsoleUtils.readNonEmptyString("Titre du projet");
        String description = ConsoleUtils.readString("Description (facultatif)");

        try {
            boolean created = projectController.createProject(titre, description, currentUser.getId());
            if (created) {
                ConsoleUtils.printSuccess("Projet « " + titre + " » créé avec succès !");
            } else {
                ConsoleUtils.printError("Impossible de créer le projet.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL lors de la création : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderProjectDetails(User currentUser) {
        ConsoleUtils.printSubHeader("DÉTAILS D'UN PROJET");
        int projectId = ConsoleUtils.readInt("Identifiant (ID) du projet");

        try {
            Project project = projectController.getProjectWithTasks(projectId, currentUser.getId());
            if (project == null) {
                ConsoleUtils.printError("Projet introuvable ou vous n'y avez pas accès.");
            } else {
                System.out.println("\n=== Projet #" + project.getId() + " : " + project.getTitre() + " ===");
                System.out.println("Description : " + (project.getDescription().isEmpty() ? "Aucune" : project.getDescription()));
                System.out.println("Créé le     : " +
                        (project.getDateCreation() != null ? project.getDateCreation().format(DATE_FORMATTER) : "-"));
                System.out.println("Avancement  : " + ConsoleUtils.formatProgressBar(project.getProgressPercentage(), 15));
                System.out.println("Tâches (" + project.getTasks().size() + ") :");

                if (project.getTasks().isEmpty()) {
                    System.out.println("   (Aucune tâche dans ce projet pour l'instant)");
                } else {
                    for (Task t : project.getTasks()) {
                        String deadline = (t.getDateEcheance() != null) ? t.getDateEcheance().toString() : "Aucune";
                        System.out.println(String.format("   [%s] (#%d) %s - Échéance: %s",
                                t.getStatusLabel(), t.getId(), t.getTitre(), deadline));
                        if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                            System.out.println("        -> " + t.getDescription());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderUpdateProject(User currentUser) {
        ConsoleUtils.printSubHeader("MODIFIER UN PROJET");
        int projectId = ConsoleUtils.readInt("ID du projet à modifier");

        try {
            Project project = projectController.getProjectWithTasks(projectId, currentUser.getId());
            if (project == null) {
                ConsoleUtils.printError("Projet introuvable ou vous n'y avez pas accès.");
                ConsoleUtils.pause();
                return;
            }

            System.out.println("Titre actuel       : " + project.getTitre());
            String newTitre = ConsoleUtils.readNonEmptyString("Nouveau titre");
            System.out.println("Description actuelle : " + project.getDescription());
            String newDesc = ConsoleUtils.readString("Nouvelle description");

            boolean updated = projectController.updateProject(projectId, newTitre, newDesc, currentUser.getId());
            if (updated) {
                ConsoleUtils.printSuccess("Projet mis à jour avec succès !");
            } else {
                ConsoleUtils.printError("Échec de la mise à jour du projet.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderDeleteProject(User currentUser) {
        ConsoleUtils.printSubHeader("SUPPRIMER UN PROJET");
        int projectId = ConsoleUtils.readInt("ID du projet à supprimer");

        try {
            Project project = projectController.getProjectWithTasks(projectId, currentUser.getId());
            if (project == null) {
                ConsoleUtils.printError("Projet introuvable ou vous n'y avez pas accès.");
                ConsoleUtils.pause();
                return;
            }

            System.out.println("Attention : la suppression du projet « " + project.getTitre() +
                    " » supprimera également toutes ses tâches associées (" + project.getTasks().size() + " tâche(s)).");
            String confirm = ConsoleUtils.readNonEmptyString("Confirmer la suppression ? (O/N)");

            if (confirm.equalsIgnoreCase("O") || confirm.equalsIgnoreCase("OUI")) {
                boolean deleted = projectController.deleteProject(projectId, currentUser.getId());
                if (deleted) {
                    ConsoleUtils.printSuccess("Projet et tâches associées supprimés.");
                } else {
                    ConsoleUtils.printError("Échec de la suppression.");
                }
            } else {
                ConsoleUtils.printInfo("Suppression annulée.");
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }
}


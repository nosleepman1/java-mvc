package views;

import Controllers.ProjectController;
import Controllers.TaskController;
import Entities.Project;
import Entities.Task;
import Entities.TaskStatus;
import Entities.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TaskView {

    private final TaskController taskController;
    private final ProjectController projectController;

    public TaskView(TaskController taskController, ProjectController projectController) {
        this.taskController = taskController;
        this.projectController = projectController;
    }

    public void showTaskMenu(User currentUser) {
        boolean inTaskMenu = true;
        while (inTaskMenu) {
            ConsoleUtils.printHeader("GESTION DES TÂCHES - " + currentUser.getNom());
            System.out.println(" 1. Voir toutes mes tâches (tous projets confondus)");
            System.out.println(" 2. Voir les tâches d'un projet spécifique");
            System.out.println(" 3. Ajouter une nouvelle tâche à un projet");
            System.out.println(" 4. Changer le statut d'une tâche (À faire / En cours / Terminé)");
            System.out.println(" 5. Modifier une tâche (titre, description, date)");
            System.out.println(" 6. Supprimer une tâche");
            System.out.println(" 0. Retour au tableau de bord");
            System.out.println("---------------------------------------------------------------");

            int choice = ConsoleUtils.readIntBetween("Votre choix", 0, 6);

            switch (choice) {
                case 1 -> renderListAllTasks(currentUser);
                case 2 -> renderListTasksByProject(currentUser);
                case 3 -> renderCreateTask(currentUser);
                case 4 -> renderChangeTaskStatus(currentUser);
                case 5 -> renderUpdateTask(currentUser);
                case 6 -> renderDeleteTask(currentUser);
                case 0 -> inTaskMenu = false;
            }
        }
    }

    public void renderListAllTasks(User currentUser) {
        ConsoleUtils.printSubHeader("TOUTES MES TÂCHES");
        try {
            List<Task> tasks = taskController.getAllTasksByUser(currentUser.getId());
            if (tasks.isEmpty()) {
                ConsoleUtils.printInfo("Aucune tâche trouvée. Ajoutez des tâches via le menu 3 !");
            } else {
                displayTaskList(tasks);
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL lors de la récupération des tâches : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderListTasksByProject(User currentUser) {
        ConsoleUtils.printSubHeader("TÂCHES D'UN PROJET");
        int projectId = ConsoleUtils.readInt("Identifiant (ID) du projet");

        try {
            List<Task> tasks = taskController.getTasksByProject(projectId, currentUser.getId());
            if (tasks.isEmpty()) {
                ConsoleUtils.printInfo("Ce projet ne contient aucune tâche pour l'instant.");
            } else {
                displayTaskList(tasks);
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderCreateTask(User currentUser) {
        ConsoleUtils.printSubHeader("AJOUTER UNE TÂCHE");
        try {
            List<Project> projects = projectController.getProjectsByUser(currentUser.getId());
            if (projects.isEmpty()) {
                ConsoleUtils.printWarning("Vous devez d'abord créer au moins un projet avant d'ajouter une tâche.");
                ConsoleUtils.pause();
                return;
            }

            System.out.println("Vos projets disponibles :");
            for (Project p : projects) {
                System.out.println(String.format("  [ID: %d] %s", p.getId(), p.getTitre()));
            }
            System.out.println();

            int projectId = ConsoleUtils.readInt("ID du projet auquel rattacher la tâche");
            String titre = ConsoleUtils.readNonEmptyString("Titre de la tâche");
            String description = ConsoleUtils.readString("Description de la tâche (facultatif)");

            System.out.println("Statut initial :");
            System.out.println(" 1. À faire");
            System.out.println(" 2. En cours");
            System.out.println(" 3. Terminé");
            int statChoice = ConsoleUtils.readIntBetween("Choix du statut", 1, 3);
            String status = switch (statChoice) {
                case 2 -> TaskStatus.EN_COURS.name();
                case 3 -> TaskStatus.TERMINE.name();
                default -> TaskStatus.A_FAIRE.name();
            };

            LocalDate deadline = ConsoleUtils.readOptionalDate("Date d'échéance");

            boolean created = taskController.createTask(projectId, titre, description, status, deadline, currentUser.getId());
            if (created) {
                ConsoleUtils.printSuccess("Tâche ajoutée avec succès au projet #" + projectId + " !");
            } else {
                ConsoleUtils.printError("Impossible de créer la tâche.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL lors de l'ajout : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderChangeTaskStatus(User currentUser) {
        ConsoleUtils.printSubHeader("CHANGER LE STATUT D'UNE TÂCHE");
        int taskId = ConsoleUtils.readInt("Identifiant (ID) de la tâche");

        try {
            Task task = taskController.getTask(taskId, currentUser.getId());
            if (task == null) {
                ConsoleUtils.printError("Tâche introuvable ou vous n'avez pas la permission.");
                ConsoleUtils.pause();
                return;
            }

            System.out.println("Tâche sélectionnée : « " + task.getTitre() + " »");
            System.out.println("Statut actuel       : " + task.getStatusLabel());
            System.out.println("Nouveau statut :");
            System.out.println(" 1. À faire");
            System.out.println(" 2. En cours");
            System.out.println(" 3. Terminé");
            int choice = ConsoleUtils.readIntBetween("Votre choix", 1, 3);

            String newStatus = switch (choice) {
                case 2 -> TaskStatus.EN_COURS.name();
                case 3 -> TaskStatus.TERMINE.name();
                default -> TaskStatus.A_FAIRE.name();
            };

            boolean updated = taskController.changeTaskStatus(taskId, newStatus, currentUser.getId());
            if (updated) {
                ConsoleUtils.printSuccess("Statut de la tâche mis à jour vers : " +
                        TaskStatus.fromString(newStatus).getLabel());
            } else {
                ConsoleUtils.printError("Échec de la mise à jour du statut.");
            }
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderUpdateTask(User currentUser) {
        ConsoleUtils.printSubHeader("MODIFIER UNE TÂCHE");
        int taskId = ConsoleUtils.readInt("Identifiant (ID) de la tâche à modifier");

        try {
            Task task = taskController.getTask(taskId, currentUser.getId());
            if (task == null) {
                ConsoleUtils.printError("Tâche introuvable ou vous n'avez pas la permission.");
                ConsoleUtils.pause();
                return;
            }

            System.out.println("Titre actuel       : " + task.getTitre());
            String newTitre = ConsoleUtils.readNonEmptyString("Nouveau titre");

            System.out.println("Description actuelle : " + task.getDescription());
            String newDesc = ConsoleUtils.readString("Nouvelle description");

            System.out.println("Statut actuel : " + task.getStatusLabel());
            System.out.println(" 1. À faire  |  2. En cours  |  3. Terminé  |  0. Garder le statut actuel");
            int statChoice = ConsoleUtils.readIntBetween("Choix statut", 0, 3);
            String status = switch (statChoice) {
                case 1 -> TaskStatus.A_FAIRE.name();
                case 2 -> TaskStatus.EN_COURS.name();
                case 3 -> TaskStatus.TERMINE.name();
                default -> task.getStatut();
            };

            System.out.println("Échéance actuelle : " + (task.getDateEcheance() != null ? task.getDateEcheance() : "Aucune"));
            LocalDate deadline = ConsoleUtils.readOptionalDate("Nouvelle date d'échéance");

            boolean updated = taskController.updateTask(taskId, newTitre, newDesc, status, deadline, currentUser.getId());
            if (updated) {
                ConsoleUtils.printSuccess("Tâche modifiée avec succès !");
            } else {
                ConsoleUtils.printError("Échec de la mise à jour.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        } catch (SQLException e) {
            ConsoleUtils.printError("Erreur SQL : " + e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void renderDeleteTask(User currentUser) {
        ConsoleUtils.printSubHeader("SUPPRIMER UNE TÂCHE");
        int taskId = ConsoleUtils.readInt("Identifiant (ID) de la tâche à supprimer");

        try {
            Task task = taskController.getTask(taskId, currentUser.getId());
            if (task == null) {
                ConsoleUtils.printError("Tâche introuvable ou vous n'avez pas la permission.");
                ConsoleUtils.pause();
                return;
            }

            String confirm = ConsoleUtils.readNonEmptyString(
                    "Confirmer la suppression de la tâche « " + task.getTitre() + " » ? (O/N)");
            if (confirm.equalsIgnoreCase("O") || confirm.equalsIgnoreCase("OUI")) {
                boolean deleted = taskController.deleteTask(taskId, currentUser.getId());
                if (deleted) {
                    ConsoleUtils.printSuccess("Tâche supprimée avec succès.");
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

    private void displayTaskList(List<Task> tasks) {
        for (Task t : tasks) {
            String badge = switch (TaskStatus.fromString(t.getStatut())) {
                case TERMINE -> "[TERMINE ]";
                case EN_COURS -> "[EN COURS]";
                case A_FAIRE -> "[À FAIRE ]";
            };
            String deadline = (t.getDateEcheance() != null) ? t.getDateEcheance().toString() : "Aucune";
            System.out.println(String.format(" %s #%-3d (Projet #%-2d) %-25s | Échéance: %s",
                    badge, t.getId(), t.getProjectId(), t.getTitre(), deadline));
            if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                System.out.println("              -> " + t.getDescription());
            }
        }
    }
}


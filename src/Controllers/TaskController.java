package Controllers;

import Entities.Project;
import Entities.Task;
import Entities.TaskStatus;
import repositories.ProjectRepository;
import repositories.TaskRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskController() throws SQLException {
        this.taskRepository = new TaskRepository();
        this.projectRepository = new ProjectRepository();
    }

    public TaskController(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<Task> getTasksByProject(int projectId, int userId) throws SQLException {
        Project project = projectRepository.findByIdAndUserId(projectId, userId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable ou vous n'y avez pas accès.");
        }
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getAllTasksByUser(int userId) throws SQLException {
        return taskRepository.findByUserId(userId);
    }

    public Task getTask(int taskId, int userId) throws SQLException {
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            return null;
        }
        return taskRepository.findById(taskId);
    }

    public boolean createTask(int projectId, String titre, String description, String statut,
                              LocalDate dateEcheance, int userId) throws SQLException {
        Project project = projectRepository.findByIdAndUserId(projectId, userId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable ou vous n'avez pas la permission.");
        }
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la tâche ne peut pas être vide.");
        }

        String validStatus = TaskStatus.fromString(statut).name();
        Task task = new Task(titre.trim(), description != null ? description.trim() : "",
                validStatus, dateEcheance, projectId);

        return taskRepository.create(task);
    }

    public boolean updateTask(int taskId, String titre, String description, String statut,
                              LocalDate dateEcheance, int userId) throws SQLException {
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            return false;
        }
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la tâche ne peut pas être vide.");
        }

        Task task = taskRepository.findById(taskId);
        if (task == null) {
            return false;
        }

        task.setTitre(titre.trim());
        task.setDescription(description != null ? description.trim() : "");
        if (statut != null && !statut.trim().isEmpty()) {
            task.setStatut(TaskStatus.fromString(statut).name());
        }
        task.setDateEcheance(dateEcheance);

        return taskRepository.update(task);
    }

    public boolean changeTaskStatus(int taskId, String newStatus, int userId) throws SQLException {
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            return false;
        }
        String validStatus = TaskStatus.fromString(newStatus).name();
        return taskRepository.updateStatus(taskId, validStatus);
    }

    public boolean deleteTask(int taskId, int userId) throws SQLException {
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            return false;
        }
        return taskRepository.delete(taskId);
    }
}


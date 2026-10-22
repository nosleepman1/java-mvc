package Controllers;

import Entities.Project;
import Entities.Task;
import repositories.ProjectRepository;
import repositories.TaskRepository;

import java.sql.SQLException;
import java.util.List;

public class ProjectController {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectController() throws SQLException {
        this.projectRepository = new ProjectRepository();
        this.taskRepository = new TaskRepository();
    }

    public ProjectController(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public List<Project> getProjectsByUser(int userId) throws SQLException {
        List<Project> projects = projectRepository.findByUserId(userId);
        for (Project project : projects) {
            List<Task> tasks = taskRepository.findByProjectId(project.getId());
            project.setTasks(tasks);
        }
        return projects;
    }

    public Project getProjectWithTasks(int projectId, int userId) throws SQLException {
        Project project = projectRepository.findByIdAndUserId(projectId, userId);
        if (project != null) {
            List<Task> tasks = taskRepository.findByProjectId(project.getId());
            project.setTasks(tasks);
        }
        return project;
    }

    public boolean createProject(String titre, String description, int userId) throws SQLException {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du projet ne peut pas être vide.");
        }
        Project project = new Project(titre.trim(), description != null ? description.trim() : "", userId);
        return projectRepository.create(project);
    }

    public boolean updateProject(int projectId, String titre, String description, int userId) throws SQLException {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du projet ne peut pas être vide.");
        }
        Project existing = projectRepository.findByIdAndUserId(projectId, userId);
        if (existing == null) {
            return false;
        }
        existing.setTitre(titre.trim());
        existing.setDescription(description != null ? description.trim() : "");
        return projectRepository.update(existing);
    }

    public boolean deleteProject(int projectId, int userId) throws SQLException {
        return projectRepository.delete(projectId, userId);
    }

    public int countProjects(int userId) throws SQLException {
        return projectRepository.countByUserId(userId);
    }
}


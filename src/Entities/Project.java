package Entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private int id;
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private int userId;
    private List<Task> tasks = new ArrayList<>();

    public Project() {
        this.dateCreation = LocalDateTime.now();
    }

    public Project(String titre, String description, int userId) {
        this.titre = titre;
        this.description = description;
        this.userId = userId;
        this.dateCreation = LocalDateTime.now();
    }

    public Project(int id, String titre, String description, LocalDateTime dateCreation, int userId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = (tasks != null) ? tasks : new ArrayList<>();
    }

    public long getCompletedTasksCount() {
        return tasks.stream()
                .filter(t -> TaskStatus.TERMINE.name().equalsIgnoreCase(t.getStatut()))
                .count();
    }

    public double getProgressPercentage() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        return (double) getCompletedTasksCount() / tasks.size() * 100.0;
    }

    @Override
    public String toString() {
        String formattedDate = (dateCreation != null)
                ? dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "-";
        return String.format("[%d] %s (Créé le: %s) - %d tâche(s)",
                id, titre, formattedDate, tasks.size());
    }
}


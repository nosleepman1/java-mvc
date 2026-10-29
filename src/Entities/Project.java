package Entities;

import java.time.LocalDateTime;

public class Project {

    private int id;
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private int userId;

    public Project() {
        this.dateCreation = LocalDateTime.now();
    }

    public Project(String titre, String description, int userId) {
        this.titre = titre;
        this.description = description;
        this.userId = userId;
        this.dateCreation = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}

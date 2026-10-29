package Entities;

import java.time.LocalDate;

public class Task {

    private int id;
    private String titre;
    private String description;
    private String statut; // "A_FAIRE", "EN_COURS", "TERMINE"
    private LocalDate dateEcheance;
    private int projectId;

    public Task() {
        this.statut = TaskStatus.A_FAIRE.name();
    }

    public Task(String titre, String description, String statut, LocalDate dateEcheance, int projectId) {
        this.titre = titre;
        this.description = description;
        this.statut = statut != null ? statut : TaskStatus.A_FAIRE.name();
        this.dateEcheance = dateEcheance;
        this.projectId = projectId;
    }

    public Task(int id, String titre, String description, String statut, LocalDate dateEcheance, int projectId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut != null ? statut : TaskStatus.A_FAIRE.name();
        this.dateEcheance = dateEcheance;
        this.projectId = projectId;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getStatusLabel() {
        return TaskStatus.fromString(statut).getLabel();
    }

    @Override
    public String toString() {
        String dateStr = (dateEcheance != null) ? dateEcheance.toString() : "Aucune";
        return String.format("[%d] %s | Statut: %s | Échéance: %s",
                id, titre, getStatusLabel(), dateStr);
    }
}


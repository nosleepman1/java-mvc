package Entities;

public enum TaskStatus {
    A_FAIRE("À faire"),
    EN_COURS("En cours"),
    TERMINE("Terminé");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

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

    public static TaskStatus fromString(String text) {
        if (text == null) {
            return A_FAIRE;
        }
        String clean = text.trim().toUpperCase()
                .replace(" ", "_")
                .replace("À", "A")
                .replace("É", "E");
        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(clean)) {
                return status;
            }
        }
        return A_FAIRE;
    }
}


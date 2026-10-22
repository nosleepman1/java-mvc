package repositories;

import Entities.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository extends BaseRepository<Task> {

    public TaskRepository() throws SQLException {
        super();
        this.tableName = "tasks";
    }

    @Override
    protected Task mapToEntity(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getInt("id"));
        task.setTitre(resultSet.getString("titre"));
        task.setDescription(resultSet.getString("description"));
        task.setStatut(resultSet.getString("statut"));

        Date date = resultSet.getDate("date_echeance");
        if (date != null) {
            task.setDateEcheance(date.toLocalDate());
        }

        task.setProjectId(resultSet.getInt("project_id"));
        return task;
    }

    public boolean create(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (titre, description, statut, date_echeance, project_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, task.getTitre());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatut());

            if (task.getDateEcheance() != null) {
                statement.setDate(4, Date.valueOf(task.getDateEcheance()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.setInt(5, task.getProjectId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        task.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public List<Task> findByProjectId(int projectId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY id ASC";
        List<Task> list = new ArrayList<>();

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, projectId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapToEntity(resultSet));
                }
            }
        }
        return list;
    }

    public List<Task> findByUserId(int userId) throws SQLException {
        String sql = """
            SELECT t.* FROM tasks t
            INNER JOIN projects p ON t.project_id = p.id
            WHERE p.user_id = ?
            ORDER BY t.project_id ASC, t.id ASC
        """;
        List<Task> list = new ArrayList<>();

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapToEntity(resultSet));
                }
            }
        }
        return list;
    }

    public boolean update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET titre = ?, description = ?, statut = ?, date_echeance = ? WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, task.getTitre());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatut());

            if (task.getDateEcheance() != null) {
                statement.setDate(4, Date.valueOf(task.getDateEcheance()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.setInt(5, task.getId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateStatus(int id, String newStatus) throws SQLException {
        String sql = "UPDATE tasks SET statut = ? WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setInt(2, id);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean isTaskOwnedByUser(int taskId, int userId) throws SQLException {
        String sql = """
            SELECT 1 FROM tasks t
            INNER JOIN projects p ON t.project_id = p.id
            WHERE t.id = ? AND p.user_id = ?
        """;

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}


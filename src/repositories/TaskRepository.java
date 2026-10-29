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
}

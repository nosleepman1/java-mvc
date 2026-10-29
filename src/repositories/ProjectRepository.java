package repositories;

import Entities.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository extends BaseRepository<Project> {

    public ProjectRepository() throws SQLException {
        super();
        this.tableName = "projects";
    }

    @Override
    protected Project mapToEntity(ResultSet resultSet) throws SQLException {
        Project project = new Project();
        project.setId(resultSet.getInt("id"));
        project.setTitre(resultSet.getString("titre"));
        project.setDescription(resultSet.getString("description"));

        Timestamp ts = resultSet.getTimestamp("date_creation");
        if (ts != null) {
            project.setDateCreation(ts.toLocalDateTime());
        }
        project.setUserId(resultSet.getInt("user_id"));
        return project;
    }

    public boolean create(Project project) throws SQLException {
        String sql = "INSERT INTO projects (titre, description, user_id) VALUES (?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, project.getTitre());
            statement.setString(2, project.getDescription());
            statement.setInt(3, project.getUserId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        project.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public List<Project> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM projects WHERE user_id = ? ORDER BY date_creation DESC";
        List<Project> list = new ArrayList<>();

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

    public Project findByIdAndUserId(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM projects WHERE id = ? AND user_id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEntity(resultSet);
                }
            }
        }
        return null;
    }

    public boolean update(Project project) throws SQLException {
        String sql = "UPDATE projects SET titre = ?, description = ? WHERE id = ? AND user_id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, project.getTitre());
            statement.setString(2, project.getDescription());
            statement.setInt(3, project.getId());
            statement.setInt(4, project.getUserId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ? AND user_id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

    public int countByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM projects WHERE user_id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}


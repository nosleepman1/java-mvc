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
}

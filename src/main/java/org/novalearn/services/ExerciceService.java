package org.novalearn.services;

import org.novalearn.Entity.Commentaire;
import org.novalearn.Entity.Exercice;
import org.novalearn.database.DatabaseConnection;
import org.novalearn.services.coursEexercice.IService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciceService implements IService<Exercice> {

    private Connection cnx;

    public ExerciceService(){
        cnx = DatabaseConnection.getConnection();
    }

    @Override
    public void create(Exercice exercice) throws SQLException {
        String query = "INSERT INTO exercice (titre, description, course_id, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, exercice.getTitre());
        ps.setString(2, exercice.getDescription());
        ps.setInt(3, exercice.getCourseId());

        ps.setDate(4, exercice.getCreatedAt());

        ps.executeUpdate();
    }

    // Les autres méthodes (update, delete, read, readAll) restent inchangées,
    // sauf si vous souhaitez également adapter la lecture des BLOB pour reconstruire les tableaux de bytes.

    @Override
    public boolean update(Exercice event) throws SQLException {
        String query = "UPDATE exercice SET titre = ?, description = ?, course_id = ?, created_at = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, event.getTitre());
        ps.setString(2, event.getDescription());
        ps.setInt(3, event.getCourseId());
        ps.setDate(4, event.getCreatedAt());
        ps.setInt(5, event.getId());
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public void delete(Exercice event) throws SQLException {
            String query = "DELETE FROM exercice WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Exercice> readAll() throws SQLException {
        List<Exercice> courses = new ArrayList<>();
        String query = "SELECT * FROM exercice";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()){
            int id = rs.getInt("id");
            String titre = rs.getString("titre");
            String description = rs.getString("description");
            int course_id = rs.getInt("course_id");
            Date created_at = rs.getDate("created_at");

            Exercice c = new Exercice(id, course_id, description, titre, created_at);
            courses.add(c);
        }
        return courses;
    }

    @Override
    public List<Commentaire> getCommentsByBlogIdPaginated(int blogId, int offset, int limit) {
        return null;
    }


    public List<Exercice> read() throws SQLException {
        return readAll();
    }



}

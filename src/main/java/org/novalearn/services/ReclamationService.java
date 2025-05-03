package org.novalearn.services;


import org.novalearn.Entity.Commentaire;
import org.novalearn.Entity.Exercice;
import org.novalearn.Entity.Reclamation;
import org.novalearn.database.DatabaseConnection;
import org.novalearn.services.coursEexercice.IService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {

    private Connection cnx;
    String Statue ="En_Attent";

    public ReclamationService() {
        cnx = DatabaseConnection.getConnection();
    }

    @Override
    public void create(Reclamation reclamation) throws SQLException {
        String query = "INSERT INTO reclamation (title, description, priority, status,genre_id,created_at) VALUES (?, ?, ?, ?,?,?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, reclamation.getTitle());
        ps.setString(2, reclamation.getDescription());
        ps.setString(3, reclamation.getPriority());

        ps.setString(4, Statue);

        ps.setInt(5, reclamation.getGenreId());

        ps.setDate(6, reclamation.getCreatedAt());

        ps.executeUpdate();
    }

    // Les autres méthodes (update, delete, read, readAll) restent inchangées,
    // sauf si vous souhaitez également adapter la lecture des BLOB pour reconstruire les tableaux de bytes.

    @Override
    public boolean update(Reclamation event) throws SQLException {
        String query = "UPDATE reclamation SET title = ?, description = ?, priority = ?, status = ?, genre_id = ?, created_at = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, event.getTitle());
        ps.setString(2, event.getDescription());
        ps.setString(3, event.getPriority());
        ps.setString(4, event.getStatus());
        ps.setInt(5, event.getGenreId());

        ps.setDate(6, event.getCreatedAt());

        ps.setInt(7, event.getId());
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public void delete(Reclamation event) throws SQLException {
        String query = "DELETE FROM reclamation WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }
    public void updateStatus(int id, String newStatus) throws SQLException {
        String sql = "UPDATE reclamation SET status = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
    @Override
    public  List<Reclamation> readAll() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String priority = rs.getString("priority");
            String status = rs.getString("status");

            int genre_id = rs.getInt("genre_id");
            Date created_at = rs.getDate("created_at");

            Reclamation c = new Reclamation(id, title, description,priority, status, created_at, genre_id);
            reclamations.add(c);
        }
        return reclamations;
    }

    @Override
    public List<Commentaire> getCommentsByBlogIdPaginated(int blogId, int offset, int limit) {
        return null;
    }


    public List<Reclamation> read() throws SQLException {
        return readAll();
    }
}

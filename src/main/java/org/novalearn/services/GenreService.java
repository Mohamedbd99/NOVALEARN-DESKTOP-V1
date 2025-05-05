package org.novalearn.services;

import org.novalearn.Entity.Commentaire;
import org.novalearn.Entity.Cours;
import org.novalearn.Entity.Exercice;
import org.novalearn.Entity.Genre;
import org.novalearn.database.DatabaseConnection;
import org.novalearn.services.coursEexercice.IService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreService implements IService<Genre> {

    private Connection cnx;

    public GenreService(){
        cnx = DatabaseConnection.getConnection();
    }

    @Override
    public void create(Genre event) throws SQLException {
        String query = "INSERT INTO genre (id, libelle, description) " +
                "VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());  // Assurez-vous que vous gérez l'ID selon votre logique (auto-incrément ou non)
        ps.setString(2, event.getLibelle());
        ps.setString(3, event.getDescription());

        ps.executeUpdate();
    }



    // Les autres méthodes (update, delete, read, readAll) restent inchangées,
    // sauf si vous souhaitez également adapter la lecture des BLOB pour reconstruire les tableaux de bytes.

    @Override
    public boolean update(Genre event) throws SQLException {
        String query = "UPDATE genre SET libelle = ?, description = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, event.getLibelle());
        ps.setString(2, event.getDescription());
        ps.setInt(3, event.getId());
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public void delete(Genre event) throws SQLException {
        String query = "DELETE FROM genre WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Genre> readAll() throws SQLException {
        List<Genre> genrs = new ArrayList<>();
        String query = "SELECT * FROM genre";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()){
            int id = rs.getInt("id");
            String libelle = rs.getString("libelle");
            String description = rs.getString("description");

            Genre c = new Genre(id, libelle, description);
            genrs.add(c);
        }
        return genrs;
    }

    @Override
    public List<Commentaire> getCommentsByBlogIdPaginated(int blogId, int offset, int limit) {
        return null;
    }




    public List<Genre> read() throws SQLException {
        return readAll();
    }


    }


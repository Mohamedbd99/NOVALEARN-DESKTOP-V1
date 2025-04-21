package org.novalearn.services.coursEexercice;

import org.novalearn.Entity.Cours;
import org.novalearn.Entity.Exercice;
import org.novalearn.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService implements IService<Cours> {

    private Connection cnx;

    public CoursService(){
        cnx = DatabaseConnection.getConnection();
    }

    @Override
    public void create(Cours event) throws SQLException {
        String query = "INSERT INTO course (id, titre, description, contenu, author_id, image, nbr_like, nombre_de_telechargements, video) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());  // Assurez-vous que vous gérez l'ID selon votre logique (auto-incrément ou non)
        ps.setString(2, event.getTitre());
        ps.setString(3, event.getDescription());
        ps.setString(4, event.getContenu());
        ps.setInt(5, event.getAuthorId());
        ps.setBytes(6, event.getImage());
        ps.setInt(7, event.getNbrLike());
        ps.setInt(8, event.getNombreDeTelechargements());
        ps.setBytes(9, event.getVideo());
        ps.executeUpdate();
    }

    public List<Exercice> getExercicesByCoursId(int coursId) throws SQLException {
        List<Exercice> exercices = new ArrayList<>();
        String req = "SELECT * FROM exercice WHERE course_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, coursId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            int id = rs.getInt("id");
            String titre = rs.getString("titre");
            String description = rs.getString("description");
            int course_id = rs.getInt("course_id");
            Date created_at = rs.getDate("created_at");

            Exercice c = new Exercice(id, course_id, description, titre, created_at);
            exercices.add(c);
        }
        return exercices;
    }

    // Les autres méthodes (update, delete, read, readAll) restent inchangées,
    // sauf si vous souhaitez également adapter la lecture des BLOB pour reconstruire les tableaux de bytes.

    @Override
    public boolean update(Cours event) throws SQLException {
        String query = "UPDATE course SET titre = ?, description = ?, contenu = ?, author_id = ?, image = ?, nombre_de_telechargements = ?, nbr_like = ?, video = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, event.getTitre());
        ps.setString(2, event.getDescription());
        ps.setString(3, event.getContenu());
        ps.setInt(4, event.getAuthorId());
        ps.setBytes(5, event.getImage());
        ps.setInt(6, event.getNombreDeTelechargements());
        ps.setInt(7, event.getNbrLike());
        ps.setBytes(8, event.getVideo());
        ps.setInt(9, event.getId());
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public void delete(Cours event) throws SQLException {
        String query = "DELETE FROM course WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Cours> readAll() throws SQLException {
        List<Cours> courses = new ArrayList<>();
        String query = "SELECT * FROM course";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()){
            int id = rs.getInt("id");
            String titre = rs.getString("titre");
            String description = rs.getString("description");
            String contenu = rs.getString("contenu");
            int author_id = rs.getInt("author_id");
            // Pour simplifier, nous ne transformons pas ici les BLOB en image,
            // vous pouvez stocker les BLOB directement dans votre objet (byte[])
            byte[] image = rs.getBytes("image");
            int nbr_like = rs.getInt("nbr_like");
            int nombre_de_telechargements = rs.getInt("nombre_de_telechargements");
            byte[] video = rs.getBytes("video");

            Cours c = new Cours(id, titre, description, contenu, author_id, image, nbr_like, nombre_de_telechargements, video);
            courses.add(c);
        }
        return courses;
    }


    public List<Cours> read() throws SQLException {
        return readAll();
    }

}

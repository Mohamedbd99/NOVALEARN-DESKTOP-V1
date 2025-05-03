package org.novalearn.services;

import org.novalearn.Entity.Blog;
import org.novalearn.Entity.Commentaire;
import org.novalearn.database.DatabaseConnection;
import org.novalearn.services.coursEexercice.IService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService  implements IService<Commentaire> {

    private Connection cnx;

    public CommentaireService() {
        cnx = DatabaseConnection.getConnection();
    }

    @Override
    public void create(Commentaire event) throws SQLException {
        String query = "INSERT INTO commentaire (id, contenu, created_at,blog_id) " +
                "VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());  // Assurez-vous que vous gérez l'ID selon votre logique (auto-incrément ou non)
        ps.setString(2, event.getContenu());
        ps.setDate(3, event.getCreatedAt());

        ps.setInt(4, event.getBlogid());


        ps.executeUpdate();
    }

   /* public List<Exercice> getExercicesByCoursId(int coursId) throws SQLException {
        List<Exercice> exercices = new ArrayList<>();
        String req = "SELECT * FROM exercice WHERE course_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, coursId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String titre = rs.getString("titre");
            String description = rs.getString("description");
            int course_id = rs.getInt("course_id");
            Date created_at = rs.getDate("created_at");

            Exercice c = new Exercice(id, course_id, description, titre, created_at);
            exercices.add(c);
        }
        return exercices;
    }*/

    // Les autres méthodes (update, delete, read, readAll) restent inchangées,
    // sauf si vous souhaitez également adapter la lecture des BLOB pour reconstruire les tableaux de bytes.

    @Override
    public boolean update(Commentaire event) throws SQLException {
        String sql = "UPDATE commentaire SET contenu = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, event.getContenu());
        ps.setInt(2, event.getId());
        System.out.println("Updating commentaire: id=" + event.getId() + ", blog_id=" + event.getBlogid());

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }


    @Override
    public void delete(Commentaire event) throws SQLException {
        String query = "DELETE FROM commentaire WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Commentaire> readAll() throws SQLException {
        List<Commentaire> cmnts = new ArrayList<>();
        String query = "SELECT * FROM commentaire";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            int id = rs.getInt("id");
            String contenu = rs.getString("contenu");
            int blog_id = rs.getInt("blog_id");
            Date created_at = rs.getDate("created_at");


            Commentaire b = new Commentaire(id, contenu, blog_id, created_at);
            cmnts.add(b);
        }
        return cmnts;
    }


    public List<Commentaire> read() throws SQLException {
        return readAll();
    }

    public static String formatTimeAgo(java.sql.Date date) {
        // Obtenez l'heure actuelle en millisecondes
        long currentTime = System.currentTimeMillis();

        // Convertir la date du commentaire en millisecondes
        long commentTime = date.getTime();

        // Calculer la différence en secondes entre l'heure actuelle et l'heure du commentaire
        long diffInSeconds = (currentTime - commentTime) / 1000;

        // Si la différence est inférieure à 60 secondes (1 minute), afficher "à l'instant"
        if (diffInSeconds < 60) {
            return "à l'instant";
        } else if (diffInSeconds < 3600) {  // Moins de 1 heure
            long minutes = diffInSeconds / 60;
            return "il y a " + minutes + "m";
        } else if (diffInSeconds < 86400) {  // Moins de 1 jour
            long hours = diffInSeconds / 3600;
            return "il y a " + hours + "h";
        } else {  // Plus de 1 jour
            long days = diffInSeconds / 86400;
            return "il y a " + days + "j";
        }
    }


    @Override
    public List<Commentaire> getCommentsByBlogIdPaginated(int blogId, int offset, int limit)  {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE blog_id = ? ORDER BY created_at LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, blogId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setContenu(rs.getString("contenu"));
                c.setCreatedAt(rs.getDate("created_at"));

                // Set other fields
                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public int getLastInsertId() throws SQLException {
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()");
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    public int countCommentairesByBlogId(int blogId) {
        String sql = "SELECT COUNT(*) FROM commentaire WHERE blog_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blogId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public boolean isBlogIdValid(int blogId) throws SQLException {
        String query = "SELECT COUNT(*) FROM blog WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne true si le blog existe
            }
        }
        return false;
    }



}
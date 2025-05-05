package org.novalearn.service;

import databaseConnection.DatabaseConnection;
import org.novalearn.Entity.Blog;
import org.novalearn.Entity.Commentaire;
import org.novalearn.service.coursEexercice.IService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogService implements IService<Blog> {

    private Connection cnx;

    public BlogService() {
        cnx = DatabaseConnection.getConnection();
    }

    @Override
    public void create(Blog event) throws SQLException {
        String query = "INSERT INTO blog (id, title, description, content, author_id, image, category, created_at,estAnonyme) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());  // Assurez-vous que vous gérez l'ID selon votre logique (auto-incrément ou non)
        ps.setString(2, event.getTitle());
        ps.setString(3, event.getDescription());
        ps.setString(4, event.getContent());
        ps.setInt(5, event.getAuthorId());
        ps.setBytes(6, event.getImage());
        ps.setString(7, event.getCategory());
        ps.setDate(8, event.getCreatedAt());
        ps.setBoolean(9, event.getEstAnonyme());

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
    public boolean update(Blog event) throws SQLException {
        String query = "UPDATE blog SET title = ?, description = ?, content = ?, author_id = ?, image = ?, category = ?, created_at = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, event.getTitle());
        ps.setString(2, event.getDescription());
        ps.setString(3, event.getContent());
        ps.setInt(4, event.getAuthorId());
        ps.setBytes(5, event.getImage());
        ps.setString(6, event.getCategory());
        ps.setDate(7, event.getCreatedAt());
        ps.setInt(8, event.getId());
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }

    @Override
    public void delete(Blog event) throws SQLException {
        String query = "DELETE FROM blog WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Blog> readAll() throws SQLException {
        List<Blog> blogs = new ArrayList<>();
        String query = "SELECT * FROM blog";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String content = rs.getString("content");
            int author_id = rs.getInt("author_id");
            // Pour simplifier, nous ne transformons pas ici les BLOB en image,
            // vous pouvez stocker les BLOB directement dans votre objet (byte[])
            byte[] image = rs.getBytes("image");
            String category = rs.getString("category");
            Date created_at = rs.getDate("created_at");
            boolean ais = rs.getBoolean("estAnonyme");



            Blog b = new Blog(id, title, description, content,category, author_id, image,created_at,ais);
            blogs.add(b);
        }
        return blogs;
    }

    @Override
    public List<Commentaire> getCommentsByBlogIdPaginated(int blogId, int offset, int limit) {
        return null;
    }


    public List<Blog> read() throws SQLException {
        return readAll();
    }
    public List<Blog> getPaginatedBlogs(int offset, int limit) throws SQLException {
        List<Blog> blogs = new ArrayList<>();
        String query = "SELECT * FROM blog ORDER BY created_at DESC LIMIT ? OFFSET ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, limit);
        ps.setInt(2, offset);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String content = rs.getString("content");
            int author_id = rs.getInt("author_id");
            byte[] image = rs.getBytes("image");
            String category = rs.getString("category");
            Date created_at = rs.getDate("created_at");
            boolean estAnonyme = rs.getBoolean("estAnonyme");

            Blog b = new Blog(id, title, description, content, category, author_id, image, created_at, estAnonyme);
            blogs.add(b);
        }
        return blogs;
    }

}
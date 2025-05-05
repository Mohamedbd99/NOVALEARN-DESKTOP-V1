package org.novalearn.Entity;

import java.sql.Date;

public class Commentaire {

    private int id;
    private String contenu;
    private int blogid;
    private Date createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getBlogid() {
        return blogid;
    }

    public void setBlogid(int blogid) {
        this.blogid = blogid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Commentaire(int id, String contenu, int blogid, Date createdAt) {
        this.id = id;
        this.contenu = contenu;
        this.blogid = blogid;
        this.createdAt = createdAt;
    }

    public Commentaire(String contenu, int blogid, Date createdAt) {
        this.contenu = contenu;
        this.blogid = blogid;
        this.createdAt = createdAt;
    }

    public Commentaire() {
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", blog_id=" + blogid +
                ", createdAt=" + createdAt +
                '}';
    }
}

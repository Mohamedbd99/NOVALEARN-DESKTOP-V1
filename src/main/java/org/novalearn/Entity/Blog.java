package org.novalearn.Entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

public class Blog {
    private int id;
    private String title;
    private String description;
    private String content;
    private String category;
    private int authorId;
    private byte[] image;
    private Date createdAt;
    private Boolean estAnonyme;

    public Blog(String titre, String description, String contenu, String category, int authorId, byte[] imageBytes, LocalDate createdAt) {
    }

    public Boolean getEstAnonyme() {
        return estAnonyme;
    }

    public void setEstAnonyme(Boolean estAnonyme) {
        this.estAnonyme = estAnonyme;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", authorId=" + authorId +
                ", image=" + Arrays.toString(image) +
                ", createdAt=" + createdAt +
                ", estAnonyme=" + estAnonyme +
                '}';
    }



    public Blog(int id, String title, String description, String content, String category, int authorId, byte[] image, Date createdAt, Boolean estAnonyme) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
        this.authorId = authorId;
        this.image = image;
        this.createdAt = createdAt;
        this.estAnonyme = estAnonyme;
    }
    public Blog(String title, String description, String content, String category, int authorId, byte[] image, LocalDate createdAt, Boolean estAnonyme) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
        this.authorId = authorId;
        this.image = image;
        this.createdAt = Date.valueOf(createdAt);
        this.estAnonyme = estAnonyme;
    }

    public Blog(int id, String title, String description, String content, String category, int authorId, byte[] image, Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
        this.authorId = authorId;
        this.image = image;
        this.createdAt = createdAt;
    }

    public Blog() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public int getAuthorId() {
        return authorId;
    }

    public byte[] getImage() {
        return image;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}

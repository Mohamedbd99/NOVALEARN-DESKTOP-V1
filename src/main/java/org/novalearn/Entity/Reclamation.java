package org.novalearn.Entity;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class Reclamation {

    private int id;

    private String title;

    private String description;

    private String status; // EN_ATTENTE, EN_COURS, RESOLUE

    private String priority; // URGENT, NORMAL, FAIBLE

    private Date createdAt;

    private int genreId;

    public Reclamation() {
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", createdAt=" + createdAt +
                ", genreId=" + genreId +
                '}';
    }

    public Reclamation(int id, String title, String description, String priority, String status, Date createdAt, int genreId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.genreId = genreId;
    }

    public Reclamation(String title, String description, String priority,  String status,LocalDate createdAt, int genreId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = Date.valueOf(createdAt);
        this.genreId = genreId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }
}
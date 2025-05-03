package org.novalearn.Entity;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Exercice {
    private int id;
    private int course_id;
    private String titre;
    private String description;
    private Date createdAt;

    // Constructeurs
    public Exercice() {}

    public Exercice(int id, int course_id, String titre, String description, Date createdAt) {
        this.id = id;
        this.course_id = course_id;
        this.titre = titre;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Exercice(String titre, String description, int course_id, LocalDate createdAt) {
        this.course_id = course_id;
        this.titre = titre;
        this.description = description;
        this.createdAt = Date.valueOf(createdAt);
    }




    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return course_id; }
    public void setCourseId(int courseId) { this.course_id = courseId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}


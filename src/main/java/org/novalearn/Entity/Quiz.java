package org.novalearn.Entity;

import javax.persistence.*;

@Entity
@Table(name = "quiz", schema = "novalearn")
public class Quiz {
    @Id
    @Column(name = "quiz_id", nullable = false)
    private String quizId;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "matiere", nullable = false)
    private String matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
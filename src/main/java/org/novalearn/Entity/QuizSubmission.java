package org.novalearn.Entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "quiz_submission", schema = "novalearn")
public class QuizSubmission {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "responses")
    private String responses;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private org.novalearn.Entity.Quiz quiz;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public org.novalearn.Entity.Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(org.novalearn.Entity.Quiz quiz) {
        this.quiz = quiz;
    }

}
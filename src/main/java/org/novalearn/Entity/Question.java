package org.novalearn.Entity;

import javax.persistence.*;

@Entity
@Table(name = "question", schema = "novalearn")
public class Question {
    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "audio")
    private String audio;

    @Column(name = "correction", nullable = false)
    private String correction;

    @Column(name = "a", nullable = false)
    private String a;

    @Column(name = "b", nullable = false)
    private String b;

    @Column(name = "c", nullable = false)
    private String c;

    @Column(name = "question", nullable = false)
    private String question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private org.novalearn.Entity.Quiz quiz;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getCorrection() {
        return correction;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public org.novalearn.Entity.Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(org.novalearn.Entity.Quiz quiz) {
        this.quiz = quiz;
    }

}
package org.novalearn.Entity;

import javax.persistence.*;

@Entity
@Table(name = "reponse", schema = "novalearn", indexes = {
        @Index(name = "IDX_5FB6DEC726C958BE", columnList = "exercice_id_id"),
        @Index(name = "IDX_5FB6DEC7602483BE", columnList = "eleve_id_id")
})
public class Reponse {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id_id")
    private Exercice exerciceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id_id")
    private org.novalearn.Entity.User eleveId;

    @Lob
    @Column(name = "contenu", nullable = false)
    private String contenu;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Exercice getExerciceId() {
        return exerciceId;
    }

    public void setExerciceId(Exercice exerciceId) {
        this.exerciceId = exerciceId;
    }

    public org.novalearn.Entity.User getEleveId() {
        return eleveId;
    }

    public void setEleveId(org.novalearn.Entity.User eleveId) {
        this.eleveId = eleveId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

}
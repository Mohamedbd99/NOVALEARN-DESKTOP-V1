package org.novalearn.Entity;

import javax.persistence.*;

@Entity
@Table(name = "course", schema = "novalearn", indexes = {
        @Index(name = "IDX_169E6FB9F675F31B", columnList = "author_id")
})
public class Course {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private org.novalearn.Entity.User author;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "contenu", nullable = false)
    private String contenu;

    @Column(name = "image")
    private String image;

    @Column(name = "nbr_like", nullable = false)
    private Integer nbrLike;

    @Column(name = "video")
    private String video;

    @Column(name = "nombre_de_telechargements", nullable = false)
    private Integer nombreDeTelechargements;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public org.novalearn.Entity.User getAuthor() {
        return author;
    }

    public void setAuthor(org.novalearn.Entity.User author) {
        this.author = author;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getNbrLike() {
        return nbrLike;
    }

    public void setNbrLike(Integer nbrLike) {
        this.nbrLike = nbrLike;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Integer getNombreDeTelechargements() {
        return nombreDeTelechargements;
    }

    public void setNombreDeTelechargements(Integer nombreDeTelechargements) {
        this.nombreDeTelechargements = nombreDeTelechargements;
    }

}
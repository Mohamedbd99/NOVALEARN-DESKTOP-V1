package org.novalearn.Entity;

import java.util.ArrayList;
import java.util.List;

public class Cours {
    private int id;
    private String titre;
    private String description;
    private String contenu;
    private int authorId;
    private byte[] image;   // changement : stockage binaire
    private int nbrLike;
    private int nombreDeTelechargements;
    private byte[] video;   // stockage binaire
    private List<Exercice> exercices = new ArrayList<>();


    // Constructeur complet
    public Cours(int id, String titre, String description, String contenu, int authorId,
                 byte[] image, int nbrLike, int nombreDeTelechargements, byte[] video) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.contenu = contenu;
        this.authorId = authorId;
        this.image = image;
        this.nbrLike = nbrLike;
        this.nombreDeTelechargements = nombreDeTelechargements;
        this.video = video;
    }

    // Constructeur sans id (pour insertion, par exemple) – adaptez selon votre logique
    public Cours(String titre, String description, String contenu, int authorId,
                 byte[] image, int nbrLike, int nombreDeTelechargements, byte[] video) {
        this.titre = titre;
        this.description = description;
        this.contenu = contenu;
        this.authorId = authorId;
        this.image = image;
        this.nbrLike = nbrLike;
        this.nombreDeTelechargements = nombreDeTelechargements;
        this.video = video;
    }

    public Cours() { }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public int getNbrLike() { return nbrLike; }
    public void setNbrLike(int nbrLike) { this.nbrLike = nbrLike; }

    public int getNombreDeTelechargements() { return nombreDeTelechargements; }
    public void setNombreDeTelechargements(int nombreDeTelechargements) { this.nombreDeTelechargements = nombreDeTelechargements; }

    public byte[] getVideo() { return video; }
    public void setVideo(byte[] video) { this.video = video; }
    @Override
    public String toString() {
        return this.titre; // ou tout autre attribut représentatif
    }
}
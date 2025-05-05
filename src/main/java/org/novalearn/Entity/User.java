package org.novalearn.Entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user", schema = "novalearn", indexes = {
        @Index(name = "unique_email", columnList = "email", unique = true),
        @Index(name = "unique_num_tel", columnList = "num_tel", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "UK_ob8kqyqqgmefl0aco34akdtpe", columnNames = {"email"}),
        @UniqueConstraint(name = "UK_3xnq3gwi1lbcuvl2ntoxa0rjp", columnNames = {"num_tel"})
})
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "difficulte")
    private String difficulte;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "genre", nullable = false)
    private String genre;

    @Column(name = "id_fils")
    private Integer idFils;

    @Column(name = "isVerified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "niv_difficulte")
    private String nivDifficulte;

    @Column(name = "nom")
    private String nom;

    @Column(name = "num_tel", nullable = false)
    private Long numTel;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "role")
    private String role;

    @Column(name = "specialite")
    private String specialite;

    @Column(name = "verificationToken")
    private String verificationToken;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private Instant resetTokenExpiry;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // Par défaut actif

    // Constructeurs
    public User() {
        this.isVerified = false;
        this.isActive = true;
    }

    public User(String nom, String prenom, String email, Long numTel, Integer age,
                String genre, String specialite, String role, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numTel = numTel;
        this.age = age;
        this.genre = genre;
        this.specialite = specialite;
        this.role = role;
        this.password = password;
        this.isVerified = false;
        this.isActive = true;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(String difficulte) {
        this.difficulte = difficulte;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getIdFils() {
        return idFils;
    }

    public void setIdFils(Integer idFils) {
        this.idFils = idFils;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getNivDifficulte() {
        return nivDifficulte;
    }

    public void setNivDifficulte(String nivDifficulte) {
        this.nivDifficulte = nivDifficulte;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getNumTel() {
        return numTel;
    }

    public void setNumTel(Long numTel) {
        this.numTel = numTel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Instant getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(Instant resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // 👇 Correction ici
    public int isActive() {
        return (this.isActive != null && this.isActive) ? 1 : 0;
    }

}

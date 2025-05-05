package org.novalearn.Entity;

import java.sql.Timestamp;

public class FailedLogin {
    private String email;
    private int attempts;
    private Timestamp lastAttempt;
    private Timestamp blockedUntil;

    // Constructeur
    public FailedLogin(String email, int attempts, Timestamp lastAttempt, Timestamp blockedUntil) {
        this.email = email;
        this.attempts = attempts;
        this.lastAttempt = lastAttempt;
        this.blockedUntil = blockedUntil;
    }

    // Getters et setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Timestamp getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(Timestamp lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public Timestamp getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(Timestamp blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    @Override
    public String toString() {
        return "FailedLogin{" +
                "email='" + email + '\'' +
                ", attempts=" + attempts +
                ", lastAttempt=" + lastAttempt +
                ", blockedUntil=" + blockedUntil +
                '}';
    }
}

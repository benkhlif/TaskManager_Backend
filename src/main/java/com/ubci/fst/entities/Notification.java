package com.ubci.fst.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;  // Le message de la notification
    private LocalDateTime dateCreation;  // La date de création de la notification

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User utilisateur;  // L'utilisateur à qui la notification est destinée

    @Column(name = "is_read", nullable = false)
    private boolean isRead;  // Nouveau champ pour savoir si la notification est lue ou non

    public Notification() {
        // Constructeur par défaut
        this.isRead = false;  // Par défaut, la notification est non lue
    }

    public Notification(String message, User utilisateur) {
        this.message = message;
        this.utilisateur = utilisateur;
        this.dateCreation = LocalDateTime.now();  // Date actuelle
        this.isRead = false;  // Par défaut, elle est non lue
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    public boolean getIsRead() {
        return isRead;
    }
}


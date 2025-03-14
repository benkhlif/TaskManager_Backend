package com.ubci.fst.entities;
 
 import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    @Enumerated(EnumType.STRING)
    private Statut statut; // A_FAIRE, EN_COURS, TERMINEE
    private LocalDateTime dateEcheance;

    
    
    @ManyToOne
    @JoinColumn(name = "projet_id")
    @JsonIgnoreProperties("taches")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
     private User assignee;

    
    
    // Constructeurs
    public Task() {}

    public Task(String titre, String description, Statut statut, LocalDateTime dateEcheance, User assignee, Projet projet) {
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.dateEcheance = dateEcheance;
        this.assignee = assignee;
        this.projet = projet;
    }


    
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public LocalDateTime getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDateTime dateEcheance) { this.dateEcheance = dateEcheance; }
    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }
    public Projet getProjet() { return projet; }
     public void setProjet(Projet projet) {
        this.projet = projet;
        if (projet != null && !projet.getTaches().contains(this)) {
            projet.getTaches().add(this); // Ajout de la tâche au projet
        }
    }
}
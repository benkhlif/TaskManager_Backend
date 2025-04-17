package com.ubci.fst.entities;

import java.util.ArrayList;
import java.util.List;

  import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
@Entity
 
 public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    @Enumerated(EnumType.STRING)
    private Statut statut;

    //mrigla
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chefProjet_id", nullable = false)
    private User chefProjet;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("projet")
    private List<Task> taches = new ArrayList<>(); 

   
     
    
    // Constructeurs
    public Projet() {}

    public Projet(String nom, String description, Statut statut, User chefProjet) {
        this.nom = nom;
        this.description = description;
        this.statut = statut;
        this.chefProjet = chefProjet;
    }

   
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public User getChefProjet() {return chefProjet;}
	public void setChefProjet(User chefProjet) { this.chefProjet = chefProjet;}

	public List<Task> getTaches() { return taches; }
    public void setTaches(List<Task> taches) {
        this.taches = taches;
        for (Task task : taches) {
            task.setProjet(this); // Met à jour le projet de chaque tâche
        }
    }}
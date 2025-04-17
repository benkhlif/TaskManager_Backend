package com.ubci.fst.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.Task;
import com.ubci.fst.entities.User;
import com.ubci.fst.repository.ProjetRepository;
import com.ubci.fst.repository.UserRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/projets")
@CrossOrigin(origins = "http://localhost:4200")
 public class ProjetController {
    @Autowired
    private  ProjetRepository projetRepository;
    @Autowired
    private  UserRepository userRepository;

    public ProjetController(ProjetRepository projetRepository, UserRepository userRepository) {
        this.projetRepository = projetRepository;
        this.userRepository = userRepository;
    }

    // 1️⃣ Récupérer tous les projets
     @GetMapping
     @EntityGraph(attributePaths = {"chefProjet"})
    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    // 2️⃣ Récupérer un projet par ID
    @GetMapping("/{id}")
    public ResponseEntity<Projet> getProjetById(@PathVariable("id") Long id) {
        return projetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
 // 3️⃣ Créer un nouvel utilisateur
 //    @PostMapping
  //    public ResponseEntity<Projet> createProjet(@RequestBody Projet projet) {
    //    Projet savedProjet = projetRepository.save(projet);
      //   return ResponseEntity.ok(savedProjet);}
    
    // 3️⃣ Créer un nouveau projet
   @PostMapping
  public ResponseEntity<Projet> createProjet(@RequestBody Projet projet) {
       Optional<User> chefProjet = userRepository.findById(projet.getChefProjet().getId());
     
        if (chefProjet.isPresent()) {
        	          projet.setChefProjet(chefProjet.get());
            Projet savedProjet = projetRepository.save(projet);
          return ResponseEntity.ok(savedProjet);
      } else {
          return ResponseEntity.badRequest().build();
       }
  }
   
    

    // 4️⃣ Mettre à jour un projet
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateProjet(@PathVariable("id") Long id, @RequestBody Projet projetDetails) {
        return projetRepository.findById(id).map(projet -> {
            projet.setNom(projetDetails.getNom());
            projet.setDescription(projetDetails.getDescription());
            projet.setStatut(projetDetails.getStatut());

            // Vérification du manager
            if (projetDetails.getChefProjet() != null && projetDetails.getChefProjet().getId() != null) {
                Optional<User> chefProjet = userRepository.findById(projetDetails.getChefProjet().getId());
                if (chefProjet.isPresent()) {
                    projet.setChefProjet(chefProjet.get());
                } else {
                    return ResponseEntity.badRequest().body("Erreur : Le chefProjet spécifié n'existe pas.");
                }
            }

            Projet updatedProjet = projetRepository.save(projet);
            return ResponseEntity.ok(updatedProjet);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur : Projet non trouvé."));
    }
    
    
    
     // 5️⃣ Supprimer un projet
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProjet(@PathVariable("id") Long id) {
        return projetRepository.findById(id).map(projet -> {
            projetRepository.delete(projet);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
    
    
    // afficher les projets pour chaque vhef de projet

@GetMapping("/chefprojet/me")
@ResponseBody
public ResponseEntity<List<Projet>> getMyProjects(Authentication authentication) {
    // Vérifier les rôles de l'utilisateur authentifié
    if (authentication.getAuthorities().stream()
            .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ChefProjet"))) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Récupérer l'email du chef de projet connecté
    String email = authentication.getName();
    
    // Trouver l'utilisateur (chef de projet) par son email
    Optional<User> chefProjetOpt = userRepository.findByEmail(email);

    if (chefProjetOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    User chefProjet = chefProjetOpt.get();

    // Récupérer les projets gérés par ce chef de projet
    List<Projet> managedProjects = projetRepository.findByChefProjet(chefProjet);

    return ResponseEntity.ok(managedProjects);
}

 

}
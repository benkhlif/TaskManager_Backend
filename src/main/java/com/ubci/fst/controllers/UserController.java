package com.ubci.fst.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.Role;
import com.ubci.fst.entities.Task;
import com.ubci.fst.entities.User;
import com.ubci.fst.repository.ProjetRepository;
import com.ubci.fst.repository.UserRepository;

@RestController //Indique que cette classe est un contrôleur REST (gère des requêtes HTTP).
@RequestMapping("/users") //Définit le chemin de base pour toutes les routes de ce contrôleur
@CrossOrigin(origins = "http://localhost:4200") //Permet aux requêtes provenant de frontend d’accéder à cette API.

public class UserController {

    @Autowired // pour Injecter repository
    private UserRepository userRepository;
  
    
    // 1️⃣ Lister tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2️⃣ Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) //@PathVariable Lie le paramètre id de l’URL à la variable id.

    { //ResponseEntity Personnaliser la réponse HTTP et Gérer les erreurs facilement
        Optional<User> optionalUser = userRepository.findById(id); //Optional<T> sert à éviter les erreurs dues à null
        if(optionalUser.isPresent()){
            return ResponseEntity.ok(optionalUser.get()); // Renvoie 200 OK avec l'utilisateur créé
        }
        return ResponseEntity.notFound().build();// Renvoie 404 si l'utilisateur n'existe pas
    }

    // 3️⃣ Créer un nouvel utilisateur
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
         User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
    
    

    // 4️⃣ Mettre à jour un utilisateur existant
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        user.setNom(userDetails.getNom());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getRole());
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // 5️⃣ Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // 6️⃣ Récupérer uniquement les utilisateurs avec le rôle "chefProjet"
    @GetMapping("/chefProjet")
    public List<User> getChefProjet() {
        return userRepository.findByRole(Role.ChefProjet);
    }
    // 6️⃣ Récupérer uniquement les utilisateurs avec le rôle "EMPLOYE"
    @GetMapping("/employee")
    public List<User> getemployee() {
        return userRepository.findByRole(Role.EMPLOYE);
    }
   
  
}  
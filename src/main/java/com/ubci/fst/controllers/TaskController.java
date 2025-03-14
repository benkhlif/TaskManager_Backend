package com.ubci.fst.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.Task;
import com.ubci.fst.entities.User;
import com.ubci.fst.repository.ProjetRepository;
import com.ubci.fst.repository.TaskRepository;
import com.ubci.fst.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository; // Pour gérer l'assignation des tâches à un utilisateur
    @Autowired
    private ProjetRepository projetRepository; 
    // 1️⃣ Récupérer la liste de toutes les tâches
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    // 2️⃣ Récupérer une tâche par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if(optionalTask.isPresent()){
            return ResponseEntity.ok(optionalTask.get());
        }
        return ResponseEntity.notFound().build();
    }
 
    // 3️⃣ Créer une nouvelle tâche
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task taskDetails) {
        // Vérifier si l'ID du projet est fourni
        if (taskDetails.getProjet() == null || taskDetails.getProjet().getId() == null) {
            return ResponseEntity.badRequest().body("Erreur : Un projet valide est requis.");
        }

        // Vérifier si le projet existe en base
        Projet projet = projetRepository.findById(taskDetails.getProjet().getId())
            .orElse(null);
        
        if (projet == null) {
            return ResponseEntity.badRequest().body("Erreur : Projet non trouvé.");
        }

        
        Task savedTask = taskRepository.save(taskDetails);
        
        return ResponseEntity.ok(savedTask);
    }
 
    // 4️⃣ Modifier une tâche existante
@PutMapping("/{id}")
public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task taskDetails) {
    Optional<Task> optionalTask = taskRepository.findById(id);
    if (!optionalTask.isPresent()) {
        return ResponseEntity.notFound().build();
    }
    Task task = optionalTask.get();
    task.setTitre(taskDetails.getTitre());
    task.setDescription(taskDetails.getDescription());
    task.setStatut(taskDetails.getStatut());
    task.setDateEcheance(taskDetails.getDateEcheance());
    
    // Mise à jour de l'utilisateur assigné si fourni
    if (taskDetails.getAssignee() != null && taskDetails.getAssignee().getId() != null) {
        Optional<User> userOptional = userRepository.findById(taskDetails.getAssignee().getId());
        if (userOptional.isPresent()) {
            task.setAssignee(userOptional.get());
        }
    }
    
    Task updatedTask = taskRepository.save(task);
    return ResponseEntity.ok(updatedTask);
}

    // 5️⃣ Supprimer une tâche
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

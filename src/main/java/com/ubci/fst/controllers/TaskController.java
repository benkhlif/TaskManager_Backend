package com.ubci.fst.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.Statut;
import com.ubci.fst.entities.Task;
import com.ubci.fst.entities.User;
import com.ubci.fst.repository.ProjetRepository;
import com.ubci.fst.repository.TaskRepository;
import com.ubci.fst.repository.UserRepository;
import com.ubci.fst.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
 

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
    @Autowired
    private JwtUtil jwtUtil; 


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
 // 6️⃣ Récupérer toutes les tâches d'un projet spécifique
    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable("projectId") Long projectId) {
        // Vérifier si le projet existe
        if (!projetRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        // Récupérer toutes les tâches associées à ce projet
        List<Task> tasks = taskRepository.findByProjetId(projectId);
        
        return ResponseEntity.ok(tasks);
    }
 // afficher le tasks d un employe
    @GetMapping("/employee/me")
    @ResponseBody
    public ResponseEntity<List<Task>> getMyTasks(Authentication authentication) {
        // Récupérer l'utilisateur connecté via Authentication
        String email = authentication.getName();

        // Trouver l'utilisateur par son email
        Optional<com.ubci.fst.entities.User> userOpt = userRepository.findByEmail(email);

        return userOpt.map(user -> ResponseEntity.ok(user.getTaches()))
                      .orElse(ResponseEntity.notFound().build());
    }
    
    
    
    
    //afficher les tasks pour le chef de projet 
    @GetMapping("/chefprojet/me/tasks")
    @ResponseBody
    public ResponseEntity<List<Task>> getTasksForMyProjects(Authentication authentication) {
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

        if (managedProjects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Récupérer les tâches associées à ces projets
        List<Task> tasks = taskRepository.findByProjetIn(managedProjects);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();  // Aucun tâche trouvée
        }

        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/dashboard/me")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        // Récupérer l'utilisateur connecté via Authentication
        String email = authentication.getName();
        
        // Trouver l'utilisateur par son email
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        // Récupérer les tâches de l'utilisateur
        List<Task> allTasks = user.getTaches();
        
        // Calculer les statistiques de base
        long tasksInProgress = allTasks.stream().filter(task -> task.getStatut() == Statut.EN_COURS).count();
        long tasksCompleted = allTasks.stream().filter(task -> task.getStatut() == Statut.TERMINEE).count();
        long tasksToDo = allTasks.stream().filter(task -> task.getStatut() == Statut.A_FAIRE).count();
        
        // Récupérer les tâches à venir (tâches dont la date d'échéance est dans le futur)
        List<Task> upcomingTasks = allTasks.stream()
                                           .filter(task -> task.getDateEcheance().isAfter(LocalDateTime.now()))
                                           .sorted(Comparator.comparing(Task::getDateEcheance))
                                           .collect(Collectors.toList());
        
        // Tâches en retard (tâches dont la date d'échéance est dépassée et statut != TERMINEE)
        List<Task> overdueTasks = allTasks.stream()
                                          .filter(task -> task.getDateEcheance().isBefore(LocalDateTime.now()) && task.getStatut() != Statut.TERMINEE)
                                          .sorted(Comparator.comparing(Task::getDateEcheance))
                                          .collect(Collectors.toList());
        
        // Calculer le pourcentage d'avancement
        long totalTasks = allTasks.size();
        double progress = (totalTasks == 0) ? 0 : (double) tasksCompleted / totalTasks * 100;
        
        // Créer une carte pour stocker les statistiques
        Map<String, Object> stats = new HashMap<>();
        stats.put("tasksInProgress", tasksInProgress);
        stats.put("tasksCompleted", tasksCompleted);
        stats.put("tasksToDo", tasksToDo);
        stats.put("upcomingTasks", upcomingTasks);  // Tâches à venir
        stats.put("overdueTasks", overdueTasks);    // Tâches en retard
        stats.put("progress", progress);
        
        return ResponseEntity.ok(stats);
    }


    
}
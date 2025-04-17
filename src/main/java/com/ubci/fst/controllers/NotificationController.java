package com.ubci.fst.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubci.fst.entities.Notification;
import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.Statut;
import com.ubci.fst.entities.Task;
import com.ubci.fst.entities.User;
import com.ubci.fst.repository.NotificationRepository;
import com.ubci.fst.repository.ProjetRepository;
import com.ubci.fst.repository.UserRepository;

@RestController
@RequestMapping("/notification")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;
 
    @GetMapping("/alerte/{id}")
    public ResponseEntity<?> getAlerteById(@PathVariable("id") Long chefProjetId) {
        List<Map<String, Object>> notifications = new ArrayList<>();

        Optional<User> chefProjetOpt = userRepository.findById(chefProjetId);
        if (chefProjetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonList("Utilisateur non trouvé."));
        }
        User chefProjet = chefProjetOpt.get();

        List<Projet> projets = projetRepository.findByChefProjet(chefProjet);

        for (Projet projet : projets) {
            List<Task> taches = projet.getTaches();
            if (taches != null && !taches.isEmpty()) {
                boolean toutesTerminees = taches.stream()
                    .allMatch(t -> Statut.TERMINEE.name().equals(t.getStatut().name()));

                if (toutesTerminees) {
                    String message = "Toutes les tâches du projet " + projet.getNom() + " sont terminées.";
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("message", message);
                    notification.put("type", "projet");
                    notification.put("id", projet.getId());  // ID du projet
                    Optional<Notification> existingNotification = notificationRepository.findByMessageAndUtilisateur(message, chefProjet);
                    notification.put("isRead", existingNotification.isPresent() ? existingNotification.get().isRead() : false);
                    notification.put("notificationId", existingNotification.isPresent() ? existingNotification.get().getId() : null);  // Ajout de l'ID de la notification
                    notifications.add(notification);
                    saveNotification(message, chefProjet);  // Enregistrer la notification
                }

                taches.stream()
                    .filter(t -> t.getDateEcheance() != null
                            && t.getDateEcheance().isBefore(LocalDateTime.now())
                            && !Statut.TERMINEE.name().equals(t.getStatut().name()))
                    .forEach(t -> {
                        String message = "La tâche " + t.getTitre() + " du projet " + projet.getNom() + " est en retard !";
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("message", message);
                        notification.put("type", "task");
                        notification.put("id", t.getId());  // ID de la tâche
                        Optional<Notification> existingNotification = notificationRepository.findByMessageAndUtilisateur(message, chefProjet);
                        notification.put("isRead", existingNotification.isPresent() ? existingNotification.get().isRead() : false);
                        notification.put("notificationId", existingNotification.isPresent() ? existingNotification.get().getId() : null);  // Ajout de l'ID de la notification
                        notifications.add(notification);
                        saveNotification(message, chefProjet);  // Enregistrer la notification
                    });
            }  
        }

        return ResponseEntity.ok(notifications);
    }

    private void saveNotification(String message, User chefProjet) {
        Optional<Notification> existingNotification = notificationRepository.findByMessageAndUtilisateur(message, chefProjet);
        if (existingNotification.isEmpty()) {
            Notification notification = new Notification(message, chefProjet);
            notification = notificationRepository.save(notification);  // Sauvegarde et obtention de l'ID
            // L'ID de la notification est maintenant sauvegardé dans notification.getId()
        }
    }


 // Nouvelle méthode pour marquer une notification comme lue
    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable("notificationId") Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification non trouvée.");
        }

        Notification notification = notificationOpt.get();
        notification.setRead(true);   
        notificationRepository.save(notification);  // Sauvegarde de la notification mise à jour

        return ResponseEntity.ok("Notification marquée comme lue.");
    }



}

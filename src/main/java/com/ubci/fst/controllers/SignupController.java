 package com.ubci.fst.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubci.fst.dto.SignupRequest;
import com.ubci.fst.entities.Role;
import com.ubci.fst.entities.User;
import com.ubci.fst.services.AuthService;
import com.ubci.fst.services.EmailService;

@RestController
@RequestMapping("/signup")
public class SignupController {
    private final AuthService authService;
    private final EmailService emailService;

    public SignupController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> signupCustomer(@RequestBody SignupRequest signupRequest) {
        // Créer un compte avec un mot de passe temporaire
        User createdCustomer = authService.createCustomer(signupRequest);

        if (createdCustomer != null) {
            createdCustomer.setRole(Role.USER); // On peut changer en MANAGER si besoin

            // Récupérer le mot de passe temporaire généré
            String temporaryPassword = createdCustomer.getPassword(); 

            // Envoyer les identifiants par email
            emailService.sendAccountDetails(
                createdCustomer.getEmail(),
                createdCustomer.getEmail(),
                temporaryPassword, // On envoie le mot de passe temporaire généré
                createdCustomer.getRole().toString()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body("Compte créé avec succès et email envoyé !");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de la création du compte");
        }
    }
}


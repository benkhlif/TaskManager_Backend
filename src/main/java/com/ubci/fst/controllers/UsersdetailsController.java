package com.ubci.fst.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubci.fst.entities.User;
import com.ubci.fst.repository.UserRepository;
@RestController

@RequestMapping("/api")
 public class UsersdetailsController {

    @Autowired // pour Injecter repository
    private UserRepository userRepository;
     
 // Ajouter une méthode pour rechercher un client par email
    @GetMapping("/searchByEmail")
    public ResponseEntity<User> getCustomerByEmail(@RequestParam String email) {
        Optional<User> customer = userRepository.findByEmail(email);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/idByEmail")
    public ResponseEntity<Long> getCustomerIdByEmail(@RequestParam String email) {
        Optional<User> customer = userRepository.findByEmail(email);
        return customer.map(cust -> ResponseEntity.ok(cust.getId()))
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }  
    
    
} 
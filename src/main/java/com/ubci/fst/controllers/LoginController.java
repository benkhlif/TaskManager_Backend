package com.ubci.fst.controllers;
 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubci.fst.dto.LoginRequest;
import com.ubci.fst.dto.LoginResponse;
import com.ubci.fst.services.UserServiceImpl;
import com.ubci.fst.utils.JwtUtil;

 
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/login")
public class LoginController {
	private final AuthenticationManager authenticationManager;

   private final UserServiceImpl customerService;

   private final JwtUtil jwtUtil;


    
   public LoginController(AuthenticationManager authenticationManager, UserServiceImpl customerService, JwtUtil jwtUtil) {
       this.authenticationManager = authenticationManager;
       this.customerService = customerService;
       this.jwtUtil = jwtUtil;
   }

   @PostMapping
   public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
       System.out.println("l utilisateur tente de se connecter: " + loginRequest.getEmail());
 
       // Authentifie l'utilisateur en vérifiant l'email et le mot de passe
       try {
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
           System.out.println("s authentifier avec succes  " + loginRequest.getEmail());
       } catch (AuthenticationException e) {
           System.out.println("echec d authentification " + loginRequest.getEmail() + ". Reason: " + e.getMessage());
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }
       // Récupère les détails de l'utilisateur après l'authentification

       UserDetails userDetails;
       try {
           // Charge l'utilisateur en fonction de son email via le service customerService
           userDetails = customerService.loadUserByUsername(loginRequest.getEmail());
           System.out.println("User details : " + userDetails.getUsername());
       } catch (UsernameNotFoundException e) {
           // Si l'utilisateur n'est pas trouvé, retourne une réponse HTTP 404 (Not Found)
           System.out.println("User non trouve: " + loginRequest.getEmail());
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       }

       // Génère un token JWT en utilisant le nom d'utilisateur et ses rôles
       String jwt = jwtUtil.generateTokenWithRoles(userDetails.getUsername(), userDetails.getAuthorities());
       System.out.println("JWT token generer: " + userDetails.getUsername());
       // Retourne la réponse contenant le token JWT

       return ResponseEntity.ok(new LoginResponse(jwt));
   }
}
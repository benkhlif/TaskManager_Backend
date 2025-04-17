package com.ubci.fst;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ubci.fst.filters.JwtRequestFilter;

import java.util.List;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

   private final JwtRequestFilter jwtRequestFilter;

   public WebSecurityConfiguration(JwtRequestFilter jwtRequestFilter) {
       this.jwtRequestFilter = jwtRequestFilter;
   }

   @Bean
   SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
               .cors(cors -> cors.configurationSource(corsConfigurationSource()))
               .csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(requests -> requests
               .requestMatchers("/login", "/signup").permitAll() // Autoriser login et signup
 
            		   .requestMatchers("/api/**").permitAll()
                       .requestMatchers( "/users/**").permitAll()
                       .requestMatchers("/tasks").authenticated()
                       .requestMatchers("/projets").authenticated() 
                        .requestMatchers("/notification").authenticated()


                       .anyRequest().authenticated())
               .sessionManagement(session -> session
                       .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
               .build();
   }

   
   
   //Cette Bean CORS est une règle de sécurité qui autorise ton frontend à communiquer avec ton backend 
   //en toute sécurité
   @Bean
   CorsConfigurationSource corsConfigurationSource() {
	   
       CorsConfiguration configuration = new CorsConfiguration();
       //configuration.setAllowedOrigins(List.of("*"));
       configuration.setAllowedOrigins(List.of("http://localhost:4200")); //Origines autorisées 
       configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); //Méthodes autorisées
       configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
       configuration.setAllowCredentials(true);    // Cela est utile pour la gestion des sessions ou des tokens d'authentification


       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", configuration);
       return source;
   }
   
   
   
//Encode les mots de passe avec BCrypt
   @Bean
   PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }
   
   
   

   @Bean
   AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
       return configuration.getAuthenticationManager();
   }
}
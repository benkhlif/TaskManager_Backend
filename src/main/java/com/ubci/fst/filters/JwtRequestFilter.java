package com.ubci.fst.filters;
 
import java.io.IOException;

 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ubci.fst.services.UserServiceImpl;
import com.ubci.fst.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	
	  private final UserServiceImpl customerService;
	    private final JwtUtil jwtUtil;
 	    public JwtRequestFilter(UserServiceImpl customerService, JwtUtil jwtUtil) {
	        this.customerService = customerService;
	        this.jwtUtil = jwtUtil;
	    }
	@Override
    // Méthode qui s'exécute à chaque requête HTTP et qui vérifie si un token JWT est présent dans l'en-tête
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
        // Récupère l'en-tête Authorization de la requête HTTP
		  String authHeader = request.getHeader("Authorization");
	        String token = null;
	        String username = null;
	        // Vérifie si l'en-tête Authorization commence par "Bearer " (indication qu'il s'agit d'un token JWT)
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            // Récupère le token sans la partie "Bearer "
	        	token = authHeader.substring(7);
	            // Extrait le nom d'utilisateur du token JWT
	            username = jwtUtil.extractUsername(token);
	        }
	        // Si un nom d'utilisateur a été extrait et que l'utilisateur n'est pas déjà authentifié
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            // Charge les détails de l'utilisateur via le service CustomerServiceImpl
	        	UserDetails userDetails = customerService.loadUserByUsername(username);
	            // Valide le token JWT en vérifiant qu'il correspond aux informations de l'utilisateur
	            if (jwtUtil.validateToken(token, userDetails)) {
	                // Si le token est valide, crée un objet d'authentification pour cet utilisateur
	                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                // Associe les détails de la requête à l'objet d'authentification
	                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                // Définit l'authentification dans le contexte de sécurité de Spring
	                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            }

	        }
	        // Continue la chaîne de filtres pour permettre à la requête de se propager
	        filterChain.doFilter(request, response);

	    }
	}
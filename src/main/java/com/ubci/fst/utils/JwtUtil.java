package com.ubci.fst.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    // Clé secrète utilisée pour signer les tokens JWT. Elle est encodée en base64 et très longue pour plus de sécurité.

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A713474375367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    // Extrait le nom d'utilisateur (subject) du token JWT

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Extrait la date d'expiration du token JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    // Méthode générique pour extraire une réclamation (claim) spécifique du token JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Récupère toutes les réclamations (claims)
        return claimsResolver.apply(claims); // Applique la fonction sur les réclamations pour extraire une information spécifique
    }
    
    // Récupère toutes les réclamations contenues dans le token JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }
    
    
    
    
    
    
    
    
    
 // Rafraîchit le token JWT en générant un nouveau token avec les mêmes informations que l'ancien
    public String refreshToken(String token) {
        if (isTokenExpired(token)) {
            final String username = extractUsername(token);
            // Vous pouvez ajouter ici des vérifications supplémentaires, comme si l'utilisateur est toujours valide
            return generateToken(username);
        }
        return token; // Retourne le token d'origine s'il n'est pas expiré
    }

    // Vérifie si le token est expiré en comparant la date d'expiration avec la date actuelle
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    
    
    
    
    
    
    
    
    // Valide le token JWT en vérifiant le nom d'utilisateur et l'expiration du token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        if (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
            return true;
        } else {
            // Si le token est expiré, on peut renvoyer un nouveau token
            String newToken = refreshToken(token);
            // Retourner un nouveau token ou gérer la session de manière appropriée
            return false; // Ou vous pouvez gérer autrement, par exemple en renvoyant un message pour demander la connexion
        }
    }

    
    
    
    
    
    
    
    
    
    
    // Génère un nouveau token JWT pour un nom d'utilisateur donné
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    // Crée un token JWT en définissant les réclamations, le sujet (nom d'utilisateur), la date d'émission et d'expiration
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 120)) // 2 heures
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // Méthode pour générer un token avec les rôles de l'utilisateur
    public String generateTokenWithRoles(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        claims.put("roles", roles);

        return createToken(claims, username);
    }
    // Méthode surchargée pour générer un token en utilisant UserDetails
    public String generateToken(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return generateTokenWithRoles(userDetails.getUsername(), authorities);
    }
}
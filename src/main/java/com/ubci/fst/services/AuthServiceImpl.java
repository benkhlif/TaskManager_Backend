package com.ubci.fst.services;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ubci.fst.dto.SignupRequest;
import com.ubci.fst.entities.Role;
import com.ubci.fst.entities.User;
import com.ubci.fst.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new SecureRandom();

    public AuthServiceImpl(UserRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createCustomer(SignupRequest signupRequest) {
        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            return null;
        }

        User customer = new User();
        BeanUtils.copyProperties(signupRequest, customer);

        // 🔹 Générer un mot de passe temporaire aléatoire
        String temporaryPassword = generateTemporaryPassword(10);
        customer.setPassword(passwordEncoder.encode(temporaryPassword)); // Hash du mot de passe

        // Assignation du rôle par défaut
        customer.setRole(Role.MANAGER);

        customer = customerRepository.save(customer);

        // ⚠️ Ne stocke pas le mot de passe temporaire en clair dans la DB, mais renvoie-le pour l'email
        customer.setPassword(temporaryPassword); 
        return customer;
    }

    private String generateTemporaryPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}

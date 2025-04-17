package com.ubci.fst.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("benkhlifoumayma0404@gmail.com"); // Adresse d'envoi

        mailSender.send(message);
    }

    // Méthode pour envoyer un e-mail contenant les identifiants
    public void sendAccountDetails(String to, String email, String password, String role) {
        String subject = "Votre compte AppUBCI a été créé avec succès !";
        String body = "Bonjour,\n\n"
                + "Votre compte a été créé avec les informations suivantes :\n"
                + "Email : " + email + "\n"
                + "Mot de passe : " + password + "\n"
                + "Rôle : " + role + "\n\n"
                + "Veuillez garder ces informations en sécurité.\n\n"
                + "Cordialement,\nL'équipe AppUBCI.";

        sendEmail(to, subject, body);
    }
}

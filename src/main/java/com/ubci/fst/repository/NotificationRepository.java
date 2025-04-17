package com.ubci.fst.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.Notification;
import com.ubci.fst.entities.User;
 
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUtilisateur(User utilisateur);

    Optional<Notification> findByMessageAndUtilisateur(String message, User utilisateur);

}
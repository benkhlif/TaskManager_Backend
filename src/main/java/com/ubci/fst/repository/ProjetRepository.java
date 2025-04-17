package com.ubci.fst.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.User;
 
@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

	List<Projet> findByChefProjet(User chefProjet);
 
}

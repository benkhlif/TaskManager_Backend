package com.ubci.fst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.Projet;
 
@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

}

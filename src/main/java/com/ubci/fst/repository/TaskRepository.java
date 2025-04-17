package com.ubci.fst.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.Projet;
import com.ubci.fst.entities.Statut;
import com.ubci.fst.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByProjetId(Long projectId);

 	List<Task> findByAssigneeId(Long managerId);

	List<Task> findByProjetIn(List<Projet> projets);
 
 
 }



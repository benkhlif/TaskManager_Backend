package com.ubci.fst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> { }

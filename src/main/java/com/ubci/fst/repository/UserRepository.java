package com.ubci.fst.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.Role;
import com.ubci.fst.entities.Task;
import com.ubci.fst.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { 

	List<User> findByRole(Role role);

    Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	  }
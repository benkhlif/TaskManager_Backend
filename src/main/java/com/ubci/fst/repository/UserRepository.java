package com.ubci.fst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubci.fst.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
 }
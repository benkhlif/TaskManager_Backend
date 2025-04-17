package com.ubci.fst.services;

  import com.ubci.fst.dto.SignupRequest;
import com.ubci.fst.entities.User;

public interface AuthService {

	User createCustomer(SignupRequest signupRequest); }

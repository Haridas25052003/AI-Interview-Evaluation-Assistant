package com.demo.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.auth.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{

	//used during login to find user by email
	Optional<User> findByEmail(String email);

	//used during registration to check if email already exists
	boolean existsByEmail(String email);

}

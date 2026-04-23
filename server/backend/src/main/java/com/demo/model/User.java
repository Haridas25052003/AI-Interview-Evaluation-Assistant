package com.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	private String role; // USER / ADMIN

	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<InterviewSetup> interviewSetups;

	// Getters & Setters
}
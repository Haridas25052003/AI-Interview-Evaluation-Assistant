package com.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class InterviewSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String status; // IN_PROGRESS / COMPLETED / ABANDONED

	private double overallScore;

	private int violationCount; // proctoring

	@ManyToOne
	@JoinColumn(name = "setup_id")
	private InterviewSetup interviewSetup;

	@OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<InterviewQuestion> questions;

	// Getters & Setters
}
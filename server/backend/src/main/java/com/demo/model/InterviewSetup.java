package com.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "interview_setup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSetup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// HR or TECHNICAL
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InterviewType interviewType;

	// --- Fields below are only relevant for TECHNICAL interviews ---

	// e.g. ["Java", "Spring Boot", "React"]
	// stored as a separate table: interview_setup_tech_stack
	@ElementCollection
	@CollectionTable(name = "interview_setup_tech_stack",
			joinColumns = @JoinColumn(name = "setup_id"))
	@Column(name = "technology")
	private List<String> techStack;

	@Enumerated(EnumType.STRING)
	private InterviewLevel interviewLevel; // BEGINNER, INTERMEDIATE, EXPERT

	private Integer yearsOfExperience; // nullable, only for TECHNICAL

	// total number of questions to ask in this interview
	@Column(nullable = false)
	private int totalQuestions = 10; // default 10

	// who created this setup
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private User user;

	// all sessions run under this setup
	@OneToMany(mappedBy = "interviewSetup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<InterviewSession> interviewSessions;
}
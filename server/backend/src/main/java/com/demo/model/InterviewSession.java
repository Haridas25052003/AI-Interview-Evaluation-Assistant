package com.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "interview_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// unique token used to identify WebSocket session
	// generated automatically when session is created
	@Column(nullable = false, unique = true)
	private String sessionToken;

	@Column(nullable = false)
	private LocalDateTime startTime;

	private LocalDateTime endTime; // null until interview is completed

	// computed at the end by averaging all AnalysisResult scores
	private Double overallScore;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SessionStatus status = SessionStatus.IN_PROGRESS;

	// tracks how many proctoring violations occurred (tab switch, face missing etc.)
	@Column(nullable = false)
	private int proctorViolationCount = 0;

	@PrePersist
	protected void onCreate() {
		this.startTime = LocalDateTime.now();
		this.sessionToken = UUID.randomUUID().toString(); // auto generate unique token
	}

	// which setup (HR or Technical config) this session belongs to
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "setup_id", nullable = false)
	@JsonBackReference
	private InterviewSetup interviewSetup;

	// all Q&A history for this session
	@OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<InterviewQuestionHistory> questions;

	// one session has one final evaluation report
	@OneToOne(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EvaluationReport evaluationReport;

	// proctoring violation logs for this session
	@OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ProctorLog> proctorLogs;
}
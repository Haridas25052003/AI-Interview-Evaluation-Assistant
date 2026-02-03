package com.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class InterviewSession {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private double overallScore;
	private String status;
	@ManyToOne
	@JoinColumn(name = "setup_id")
	private InterviewSetup interviewSetup;
	
	@OneToMany(mappedBy = "interviewSession")
	private List<InterviewQuestionHistory> questions;

	public List<InterviewQuestionHistory> getQuestions() {
		return questions;
	}
	public void setQuestions(List<InterviewQuestionHistory> questions) {
		this.questions = questions;
	}
	public InterviewSetup getInterviewSetup() {
		return interviewSetup;
	}
	public void setInterviewSetup(InterviewSetup interviewSetup) {
		this.interviewSetup = interviewSetup;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public double getOverallScore() {
		return overallScore;
	}
	public void setOverallScore(double overallScore) {
		this.overallScore = overallScore;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

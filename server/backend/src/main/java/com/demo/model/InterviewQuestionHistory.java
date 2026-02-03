package com.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class InterviewQuestionHistory {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String questionText;
	private String userAnswer;
	private int questionNumber;
	private LocalDateTime answeredAt;
	
	@ManyToOne
	@JoinColumn(name = "session_id")
	private InterviewSession interviewSession;

	@OneToOne(mappedBy = "questionHistory")
	private AnalysisResult analysisResult;

	public InterviewSession getInterviewSession() {
		return interviewSession;
	}
	public void setInterviewSession(InterviewSession interviewSession) {
		this.interviewSession = interviewSession;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public AnalysisResult getAnalysisResult() {
		return analysisResult;
	}
	public void setAnalysisResult(AnalysisResult analysisResult) {
		this.analysisResult = analysisResult;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getUserAnswer() {
		return userAnswer;
	}
	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}
	public int getQuestionNumber() {
		return questionNumber;
	}
	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}
	public LocalDateTime getAnsweredAt() {
		return answeredAt;
	}
	public void setAnsweredAt(LocalDateTime answeredAt) {
		this.answeredAt = answeredAt;
	}
}

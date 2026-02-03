package com.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class AnalysisResult {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	//indicator scores
	private int relevanceScore;
	private int conceptScore;
	private int clarityScore;
	private int grammerScore;
	private int completeScore;
	
	private double overallScore;
	private String feedbackSummary;
	
	@OneToOne
	@JoinColumn(name = "question_history_id")
	private InterviewQuestionHistory questionHistory;

	public InterviewQuestionHistory getQuestionHistory() {
		return questionHistory;
	}
	public void setQuestionHistory(InterviewQuestionHistory questionHistory) {
		this.questionHistory = questionHistory;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRelevanceScore() {
		return relevanceScore;
	}
	public void setRelevanceScore(int relevanceScore) {
		this.relevanceScore = relevanceScore;
	}
	public int getConceptScore() {
		return conceptScore;
	}
	public void setConceptScore(int conceptScore) {
		this.conceptScore = conceptScore;
	}
	public int getClarityScore() {
		return clarityScore;
	}
	public void setClarityScore(int clarityScore) {
		this.clarityScore = clarityScore;
	}
	public int getGrammerScore() {
		return grammerScore;
	}
	public void setGrammerScore(int grammerScore) {
		this.grammerScore = grammerScore;
	}
	public int getCompleteScore() {
		return completeScore;
	}
	public void setCompleteScore(int completeScore) {
		this.completeScore = completeScore;
	}
	public double getOverallScore() {
		return overallScore;
	}
	public void setOverallScore(double overallScore) {
		this.overallScore = overallScore;
	}
	public String getFeedbackSummary() {
		return feedbackSummary;
	}
	public void setFeedbackSummary(String feedbackSummary) {
		this.feedbackSummary = feedbackSummary;
	}	
}

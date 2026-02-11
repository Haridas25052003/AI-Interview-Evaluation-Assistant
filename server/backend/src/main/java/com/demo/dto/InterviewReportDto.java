package com.demo.dto;

import java.util.List;

public class InterviewReportDto {

	private int sessionId;
	private String level;
	private String language;
	private double overallScore;
	private List<QuestionReportDto> questions;
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public double getOverallScore() {
		return overallScore;
	}
	public void setOverallScore(double overallScore) {
		this.overallScore = overallScore;
	}
	public List<QuestionReportDto> getQuestions() {
		return questions;
	}
	public void setQuestions(List<QuestionReportDto> questions) {
		this.questions = questions;
	}

}

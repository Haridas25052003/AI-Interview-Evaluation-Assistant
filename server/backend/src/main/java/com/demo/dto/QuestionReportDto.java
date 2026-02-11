package com.demo.dto;

public class QuestionReportDto {

	private String question;
	private String answer;
	private int relevance;
	private int concept;
	private int clarity;
	private int grammer;
	private int completeness;
	private double score;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getRelevance() {
		return relevance;
	}
	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}
	public int getConcept() {
		return concept;
	}
	public void setConcept(int concept) {
		this.concept = concept;
	}
	public int getClarity() {
		return clarity;
	}
	public void setClarity(int clarity) {
		this.clarity = clarity;
	}
	public int getGrammer() {
		return grammer;
	}
	public void setGrammer(int grammer) {
		this.grammer = grammer;
	}
	public int getCompleteness() {
		return completeness;
	}
	public void setCompleteness(int completeness) {
		this.completeness = completeness;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
}

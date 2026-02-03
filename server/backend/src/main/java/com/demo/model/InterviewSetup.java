package com.demo.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class InterviewSetup {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String interviewLevel;
	private String programmingLanguage;
	@ManyToOne
    @JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "interviewSetup")
	private List<InterviewSession> interviewSessions;
	
	
	//getter and setter
	public List<InterviewSession> getInterviewSessions() {
		return interviewSessions;
	}
	public void setInterviewSessions(List<InterviewSession> interviewSessions) {
		this.interviewSessions = interviewSessions;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getInterviewLevel() {
		return interviewLevel;
	}
	public void setInterviewLevel(String interviewLevel) {
		this.interviewLevel = interviewLevel;
	}
	public String getProgrammingLanguage() {
		return programmingLanguage;
	}
	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}
	
	
}

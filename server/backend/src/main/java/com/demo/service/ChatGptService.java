package com.demo.service;

public interface ChatGptService {

	String generateQuestion(String language,String level);
	
	String evaluateAnswer(String prompt);
}

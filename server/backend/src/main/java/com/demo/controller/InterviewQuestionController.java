package com.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.InterviewQuestionHistory;
import com.demo.service.InterviewQuestionHistoryService;

@RestController
public class InterviewQuestionController {

	@Autowired
	private InterviewQuestionHistoryService service;
	
	@PostMapping("/save")
	public InterviewQuestionHistory saveQuestion(
			@RequestBody InterviewQuestionHistory question) {
		
		question.setAnsweredAt(LocalDateTime.now());
		return service.saveQuestion(question);
	}
}

package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.ChatGptService;

@RestController
public class QuestionController {

	@Autowired
	private ChatGptService cgs;
	
	@GetMapping("/generate")
	public String generateQuestion(@RequestParam String language,
			@RequestParam String level) {
		return cgs.generateQuestion(language, level);
	}
	
}

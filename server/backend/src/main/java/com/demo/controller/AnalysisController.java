package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.AnalysisResult;
import com.demo.service.AnalysisResultService;

@RestController
public class AnalysisController {

	@Autowired
	private AnalysisResultService service;
	
	@PostMapping("/evaluate")
	public AnalysisResult m1(@RequestParam int questionId) {
		return service.analyzeAnswer(questionId);
	}
}

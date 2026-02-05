package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.InterviewSetup;
import com.demo.service.InterviewSetupService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class InterviewSetupController {

	@Autowired
	private InterviewSetupService is;
	
	@PostMapping("/create")
	public InterviewSetup m1(@RequestBody InterviewSetup setup) {
		return is.saveSetup(setup);
	}
}

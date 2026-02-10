package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.InterviewSession;
import com.demo.service.InterviewSessionService;

@RestController
public class InterviewSessionController {

	@Autowired
	private InterviewSessionService iss;
	
	//start interview session
	@PostMapping("/start")
	public InterviewSession m1(@RequestBody InterviewSession session) {
		return iss.startSession(session);
	}
	
	//end interview session
	@PostMapping("/end")
	public InterviewSession endSession(@RequestParam int sessionId,
			@RequestParam double overallScore) {
		return iss.endSession(sessionId, overallScore);
	}
	
	@PostMapping("/finish")
	public String finishSession(@RequestParam int sessionId) {
	    iss.finishInterviewSession(sessionId);
	    return "Interview session completed";
	}

}

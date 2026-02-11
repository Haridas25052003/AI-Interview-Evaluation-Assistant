package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.dto.InterviewReportDto;
import com.demo.service.InterviewReportService;

@RestController
public class InterviewReportController {

	@Autowired
	private InterviewReportService reportService;
	
	@GetMapping
	public InterviewReportDto getReport(@RequestParam int sessionId) {
		return reportService.getInterviewReport(sessionId);
	}
}

package com.demo.service;

import com.demo.model.InterviewSession;

public interface InterviewSessionService {

	InterviewSession startSession(InterviewSession session);
	
	InterviewSession endSession(int sessionId,double overallScore);
	
	public void finishInterviewSession(int sessionId);

}

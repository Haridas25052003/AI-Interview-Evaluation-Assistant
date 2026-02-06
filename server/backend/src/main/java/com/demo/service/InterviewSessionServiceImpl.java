package com.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.InterviewSessionDao;
import com.demo.model.InterviewSession;

@Service
public class InterviewSessionServiceImpl implements InterviewSessionService{

	@Autowired
	private InterviewSessionDao isd;

	@Override
	public InterviewSession startSession(InterviewSession session) {
		session.setStartTime(LocalDateTime.now());
		session.setStatus("In_progress");
		return isd.save(session);
	}

	@Override
	public InterviewSession endSession(int sessionId, double overallScore) {
		InterviewSession session=isd.findById(sessionId).orElse(null);
		
		if(session!=null) {
			session.setEndTime(LocalDateTime.now());
			session.setOverallScore(overallScore);
			session.setStatus("completed");
			return isd.save(session);
		}
		return null;
	}
}

package com.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.AnalysisResultDao;
import com.demo.dao.InterviewSessionDao;
import com.demo.model.AnalysisResult;
import com.demo.model.InterviewSession;

@Service
public class InterviewSessionServiceImpl implements InterviewSessionService{

	@Autowired
	private InterviewSessionDao isd;
	
	@Autowired
	private AnalysisResultDao asd;

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
	
	@Override
	public void finishInterviewSession(int sessionId) {

	    InterviewSession session =
	            isd.findById(sessionId).orElse(null);

	    if (session == null) return;

	    List<AnalysisResult> results =
	            asd
	            .findByQuestionHistory_InterviewSession_Id(sessionId);

	    if (results.isEmpty()) return;

	    double total = 0;
	    for (AnalysisResult r : results) {
	        total += r.getOverallScore();
	    }

	    double average = total / results.size();

	    session.setOverallScore(average);
	    session.setStatus("COMPLETED");

	    isd.save(session);
	}

}

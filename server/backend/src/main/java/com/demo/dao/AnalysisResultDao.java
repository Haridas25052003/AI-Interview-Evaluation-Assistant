package com.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.AnalysisResult;
import com.demo.model.InterviewQuestionHistory;

public interface AnalysisResultDao extends JpaRepository<AnalysisResult,Integer>{

	//Optional<InterviewQuestionHistory> findByQuestionHistoryId(int questionHistoryId);

	//Optional<InterviewQuestionHistory> findByQuestionHistoryId(int questionHistoryId);
	
	 Optional<AnalysisResult> findByQuestionHistoryId(int questionHistoryId);
	 
	 List<AnalysisResult> findByQuestionHistory_InterviewSession_Id(int sessionId);
	 
	// List<AnalysisResult> findByQuestionHistory_InterviewSession_Id(int sessionId);



}

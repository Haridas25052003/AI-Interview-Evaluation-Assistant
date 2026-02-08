package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.InterviewQuestionHistoryDao;
import com.demo.model.InterviewQuestionHistory;

@Service
public class InterviewQuestionHistoryServiceImpl implements InterviewQuestionHistoryService{
	
	@Autowired
	private InterviewQuestionHistoryDao iqd;

	@Override
	public InterviewQuestionHistory saveQuestion(InterviewQuestionHistory question) {
		
		return iqd.save(question);
	}

	@Override
	public InterviewQuestionHistory submitAnswer(int questionId, String userAnswer) {
		InterviewQuestionHistory question =
	            iqd.findById(questionId).orElse(null);

	    if (question != null) {
	        question.setUserAnswer(userAnswer);
	        question.setAnsweredAt(java.time.LocalDateTime.now());
	        return iqd.save(question);
	    }
		return null;
	}
	

}

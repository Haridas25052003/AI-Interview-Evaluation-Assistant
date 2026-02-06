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
	

}

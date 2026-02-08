package com.demo.service;

import com.demo.model.InterviewQuestionHistory;

public interface InterviewQuestionHistoryService {

	InterviewQuestionHistory saveQuestion(InterviewQuestionHistory question);
	
	InterviewQuestionHistory submitAnswer(int questionId, String userAnswer);

}

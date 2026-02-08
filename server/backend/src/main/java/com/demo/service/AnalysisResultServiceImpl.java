package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.AnalysisResultDao;
import com.demo.dao.InterviewQuestionHistoryDao;
import com.demo.model.AnalysisResult;
import com.demo.model.InterviewQuestionHistory;

@Service
public class AnalysisResultServiceImpl implements AnalysisResultService{

	@Autowired
	private ChatGptService chatGptService;
	
	@Autowired
	private InterviewQuestionHistoryDao questionDao;
	
	@Autowired
	private AnalysisResultDao ard;

	@Override
	public AnalysisResult analyzeAnswer(int questionHistoryId) {

	    InterviewQuestionHistory q =
	            questionDao.findById(questionHistoryId).orElse(null);

	    if (q == null) return null;
	    AnalysisResult result =
	            ard.findByQuestionHistoryId(questionHistoryId)
	               .orElse(new AnalysisResult());


	    result.setQuestionHistory(q);

	    String prompt =
	            "Evaluate the following interview answer.\n\n" +
	            "Question: " + q.getQuestionText() + "\n" +
	            "Answer: " + q.getUserAnswer() + "\n\n" +
	            "Give scores (0-10) for:\n" +
	            "1. Relevance\n" +
	            "2. Concept understanding\n" +
	            "3. Clarity\n" +
	            "4. Grammar\n" +
	            "5. Completeness\n" +
	            "Also give overall score and short feedback.\n" +
	            "Respond in JSON format.";

	    String aiResponse = chatGptService.evaluateAnswer(prompt);
	  
	    result.setRelevanceScore(8);
	    result.setConceptScore(7);
	    result.setClarityScore(8);
	    result.setGrammerScore(9);
	    result.setCompleteScore(7);
	    result.setOverallScore(7.8);
	    result.setFeedbackSummary("Good understanding, improve depth.");

	    
	    return ard.save(result);
	}
	
	
	

}

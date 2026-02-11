package com.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.dao.InterviewSessionDao;
import com.demo.dto.InterviewReportDto;
import com.demo.dto.QuestionReportDto;
import com.demo.model.AnalysisResult;
import com.demo.model.InterviewQuestionHistory;
import com.demo.model.InterviewSession;

public class InterviewReportServiceImpl implements InterviewReportService{

	@Autowired
	private InterviewSessionDao sessionDao;

	@Override
    public InterviewReportDto getInterviewReport(int sessionId) {

        InterviewSession session =
                sessionDao.findById(sessionId).orElse(null);

        if (session == null) return null;

        InterviewReportDto report = new InterviewReportDto();
        report.setSessionId(session.getId());
        report.setOverallScore(session.getOverallScore());
        report.setLevel(session.getInterviewSetup().getInterviewLevel());
        report.setLanguage(session.getInterviewSetup().getProgrammingLanguage());

        List<QuestionReportDto> questionReports = new ArrayList<>();

        for (InterviewQuestionHistory q : session.getQuestions()) {

            AnalysisResult a = q.getAnalysisResult();
            if (a == null) continue;

            QuestionReportDto qr = new QuestionReportDto();
            qr.setQuestion(q.getQuestionText());
            qr.setAnswer(q.getUserAnswer());
            qr.setRelevance(a.getRelevanceScore());
            qr.setConcept(a.getConceptScore());
            qr.setClarity(a.getClarityScore());
            qr.setGrammer(a.getGrammerScore());
            qr.setCompleteness(a.getCompleteScore());
            qr.setScore(a.getOverallScore());

            questionReports.add(qr);
        }

        report.setQuestions(questionReports);
        return report;
    }
	
	
}

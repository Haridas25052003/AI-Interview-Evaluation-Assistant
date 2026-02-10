package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.AnalysisResultDao;
import com.demo.dao.InterviewQuestionHistoryDao;
import com.demo.model.AnalysisResult;
import com.demo.model.InterviewQuestionHistory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class AnalysisResultServiceImpl implements AnalysisResultService {

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private InterviewQuestionHistoryDao questionDao;

    @Autowired
    private AnalysisResultDao ard;

    @Override
    public AnalysisResult analyzeAnswer(int questionHistoryId) {

        // 1️⃣ Fetch question
        InterviewQuestionHistory q =
                questionDao.findById(questionHistoryId).orElse(null);

        if (q == null) return null;

        // 2️⃣ Fetch existing analysis OR create new
        AnalysisResult result =
                ard.findByQuestionHistoryId(questionHistoryId)
                   .orElse(new AnalysisResult());

        // 3️⃣ Set relation
        result.setQuestionHistory(q);

        // 4️⃣ AI prompt
        String prompt =
            "You are an interview evaluator.\n" +
            "Evaluate the following answer and respond ONLY in valid JSON.\n\n" +
            "Question: " + q.getQuestionText() + "\n" +
            "Answer: " + q.getUserAnswer() + "\n\n" +
            "JSON format:\n" +
            "{\n" +
            "  \"relevance\": number,\n" +
            "  \"concept\": number,\n" +
            "  \"clarity\": number,\n" +
            "  \"grammar\": number,\n" +
            "  \"completeness\": number,\n" +
            "  \"overall\": number,\n" +
            "  \"feedback\": string\n" +
            "}";

        // 5️⃣ Call ChatGPT
        String aiResponse = chatGptService.evaluateAnswer(prompt);

        try {
            // 6️⃣ Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(aiResponse);

            result.setRelevanceScore(json.get("relevance").asInt());
            result.setConceptScore(json.get("concept").asInt());
            result.setClarityScore(json.get("clarity").asInt());
            result.setGrammerScore(json.get("grammar").asInt());
            result.setCompleteScore(json.get("completeness").asInt());
            result.setOverallScore(json.get("overall").asDouble());
            result.setFeedbackSummary(json.get("feedback").asText());

        } catch (Exception e) {
            // fallback (safe default)
            result.setRelevanceScore(0);
            result.setConceptScore(0);
            result.setClarityScore(0);
            result.setGrammerScore(0);
            result.setCompleteScore(0);
            result.setOverallScore(0);
            result.setFeedbackSummary("AI evaluation failed.");
        }

        // 7️⃣ Save (insert or update)
        return ard.save(result);
    }
}

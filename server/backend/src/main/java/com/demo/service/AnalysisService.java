package com.demo.service;

import com.demo.gpt.OpenAIClient;
import com.demo.model.AnalysisResult;
import com.demo.model.InterviewQuestionHistory;
import com.demo.repository.AnalysisResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final OpenAIClient openAIClient;
    private final AnalysisResultRepository analysisResultRepository;
    private final ObjectMapper objectMapper;

    // analyzes a single answer and saves the result
    public AnalysisResult analyzeAnswer(InterviewQuestionHistory questionHistory) {

        // if skipped — return zero scores immediately without calling GPT
        if (questionHistory.isWasSkipped()) {
            return saveSkippedAnalysis(questionHistory);
        }

        String systemPrompt = """
            You are an expert interview evaluator. Analyze the candidate's answer
            and return a JSON object with exactly these fields (scores from 0 to 10):
            {
              "relevanceScore": <int>,
              "conceptScore": <int>,
              "clarityScore": <int>,
              "grammarScore": <int>,
              "completenessScore": <int>,
              "overallScore": <double>,
              "feedbackSummary": "<one sentence feedback>"
            }
            Return only valid JSON. No explanation, no markdown, no extra text.
            """;

        String userPrompt = String.format("""
            Question: %s
            Candidate's Answer: %s
            
            Evaluate this answer and return the JSON scores.
            """,
            questionHistory.getQuestionText(),
            questionHistory.getUserAnswer()
        );

        try {
            String gptResponse = openAIClient.chat(systemPrompt, userPrompt);
            JsonNode json = objectMapper.readTree(gptResponse);

            AnalysisResult result = AnalysisResult.builder()
                    .relevanceScore(json.path("relevanceScore").asInt())
                    .conceptScore(json.path("conceptScore").asInt())
                    .clarityScore(json.path("clarityScore").asInt())
                    .grammarScore(json.path("grammarScore").asInt())
                    .completenessScore(json.path("completenessScore").asInt())
                    .overallScore(json.path("overallScore").asDouble())
                    .feedbackSummary(json.path("feedbackSummary").asText())
                    .skipped(false)
                    .questionHistory(questionHistory)
                    .build();

            return analysisResultRepository.save(result);

        } catch (Exception e) {
            log.error("Failed to analyze answer: {}", e.getMessage());
            throw new RuntimeException("Answer analysis failed: " + e.getMessage());
        }
    }

    // compute final score for entire session (average of all non-skipped answers)
    public double computeFinalScore(Long sessionId) {
        Double avg = analysisResultRepository.computeAverageScoreBySessionId(sessionId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    // save zero-score analysis for skipped questions
    private AnalysisResult saveSkippedAnalysis(InterviewQuestionHistory questionHistory) {
        AnalysisResult result = AnalysisResult.builder()
                .relevanceScore(0)
                .conceptScore(0)
                .clarityScore(0)
                .grammarScore(0)
                .completenessScore(0)
                .overallScore(0.0)
                .feedbackSummary("Question was skipped by the candidate.")
                .skipped(true)
                .questionHistory(questionHistory)
                .build();

        return analysisResultRepository.save(result);
    }
}
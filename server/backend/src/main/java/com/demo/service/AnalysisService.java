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

    public AnalysisResult analyzeAnswer(InterviewQuestionHistory questionHistory) {

        if (questionHistory.isWasSkipped()) {
            return saveSkippedAnalysis(questionHistory);
        }

        String systemPrompt = """
            You are a fair and encouraging interview evaluator at a reputed tech company.
            Your job is to evaluate a candidate's spoken interview answer.

            IMPORTANT CONTEXT:
            - The candidate's answer was captured via Speech-to-Text technology.
            - Speech-to-Text may introduce minor errors like missing punctuation,
              small word mismatches, or filler words like "um", "uh", "like".
            - Do NOT penalize for these speech transcription artifacts.
            - Evaluate the MEANING and CONTENT of the answer, not the writing style.
            - Be fair and generous — if the candidate clearly understands the concept,
              reward them appropriately.

            SCORING GUIDE (use this strictly):
            9-10 → Excellent: answer is accurate, complete, and well explained
            7-8  → Good: answer is mostly correct with minor gaps
            5-6  → Average: answer shows basic understanding but lacks depth
            3-4  → Below average: partial understanding, missing key points
            1-2  → Poor: mostly incorrect or very vague
            0    → No answer or completely irrelevant

            PARAMETER DEFINITIONS:
            - relevanceScore: Did the answer directly address the question asked? (9-10 if on-topic)
            - conceptScore: Does the candidate understand the underlying concept correctly?
            - clarityScore: Is the core idea of the answer understandable and clear?
              NOTE: Ignore filler words, missing punctuation — focus on whether the meaning is clear.
            - grammarScore: Evaluate SPOKEN grammar only — ignore missing commas, periods, capitalization.
              Give 8-10 if the spoken sentences make grammatical sense even if transcription looks rough.
              Only penalize if the sentence structure itself is broken or incomprehensible.
            - completenessScore: Does the answer cover the main points needed to answer the question?
              A 2-3 line answer that covers the key point should score 7-8, not 3-4.

            CRITICAL RULES:
            - Never give below 5 for grammarScore just because the text has no punctuation.
            - Never give below 6 for clarityScore if the answer meaning is understandable.
            - If the candidate gives a correct and relevant answer, overall score must be 7 or above.
            - overallScore must be the weighted average:
              (relevanceScore * 0.25) + (conceptScore * 0.30) + (clarityScore * 0.20)
              + (grammarScore * 0.10) + (completenessScore * 0.15)
            - feedbackSummary must be one encouraging sentence mentioning what was good
              and one specific thing to improve.

            Return ONLY this JSON — no explanation, no markdown, no extra text:
            {
              "relevanceScore": <int 0-10>,
              "conceptScore": <int 0-10>,
              "clarityScore": <int 0-10>,
              "grammarScore": <int 0-10>,
              "completenessScore": <int 0-10>,
              "overallScore": <double 0.0-10.0>,
              "feedbackSummary": "<one encouraging sentence with one improvement tip>"
            }
            """;

        String userPrompt = String.format("""
            Interview Question:
            "%s"

            Candidate's Spoken Answer (captured via Speech-to-Text):
            "%s"

            Evaluate this answer fairly using the scoring guide above.
            Remember this is spoken English captured by STT — be lenient on grammar and punctuation.
            If the answer is conceptually correct, score it 7 or above.
            Return only the JSON.
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

    public double computeFinalScore(Long sessionId) {
        Double avg = analysisResultRepository.computeAverageScoreBySessionId(sessionId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

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
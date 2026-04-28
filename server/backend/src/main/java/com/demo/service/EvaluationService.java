package com.demo.service;

import com.demo.dto.EvaluationReportResponseDTO;
import com.demo.gpt.OpenAIClient;
import com.demo.model.*;
import com.demo.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final OpenAIClient openAIClient;
    private final EvaluationReportRepository evaluationReportRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionHistoryRepository questionHistoryRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final ProctorLogRepository proctorLogRepository;
    private final ObjectMapper objectMapper;

    // main method — called after interview ends
    // generates and saves the full evaluation report
    public EvaluationReport generateReport(String sessionToken) {

        // avoid duplicate report generation
        InterviewSession session = interviewSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionToken));

        if (evaluationReportRepository.existsByInterviewSessionId(session.getId())) {
            log.info("Report already exists for session: {}", sessionToken);
            return evaluationReportRepository.findByInterviewSessionId(session.getId()).get();
        }

        // gather all data
        List<InterviewQuestionHistory> allQuestions =
                questionHistoryRepository.findByInterviewSessionIdOrderByQuestionNumberAsc(session.getId());

        List<InterviewQuestionHistory> answeredQuestions =
                questionHistoryRepository.findAnsweredQuestionsBySessionId(session.getId());

        List<AnalysisResult> allAnalysis =
                analysisResultRepository.findAllBySessionId(session.getId());

        int totalViolations = proctorLogRepository.countByInterviewSessionId(session.getId());

        // compute final score
        double overallScore = analysisResultRepository
                .computeAverageScoreBySessionId(session.getId()) != null
                ? analysisResultRepository.computeAverageScoreBySessionId(session.getId())
                : 0.0;

        // determine performance band
        String performanceBand = getPerformanceBand(overallScore);

        // call GPT to generate strengths, weaknesses, recommendation
        GPTEvaluationResult gptResult = callGPTForEvaluation(session, allQuestions, allAnalysis);

        // build and save report
        EvaluationReport report = EvaluationReport.builder()
                .overallScore(Math.round(overallScore * 100.0) / 100.0)
                .performanceBand(performanceBand)
                .strengths(gptResult.strengths)
                .areasToImprove(gptResult.areasToImprove)
                .recommendation(gptResult.recommendation)
                .totalQuestions(allQuestions.size())
                .answeredQuestions(answeredQuestions.size())
                .skippedQuestions(allQuestions.size() - answeredQuestions.size())
                .totalViolations(totalViolations)
                .interviewSession(session)
                .build();

        return evaluationReportRepository.save(report);
    }

    // get report for evaluation page
    public EvaluationReport getReportBySessionToken(String sessionToken) {
        InterviewSession session = interviewSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionToken));

        return evaluationReportRepository.findByInterviewSessionId(session.getId())
                .orElseThrow(() -> new RuntimeException("Report not yet generated for this session"));
    }

    // get all reports for a user (history page)
    public List<EvaluationReport> getReportsByUserId(Long userId) {
        return evaluationReportRepository.findAllByUserId(userId);
    }

    // calls GPT with the full interview transcript to get overall feedback
    private GPTEvaluationResult callGPTForEvaluation(InterviewSession session,
                                                      List<InterviewQuestionHistory> questions,
                                                      List<AnalysisResult> analysis) {
        String systemPrompt = """
            You are a senior hiring manager evaluating a completed interview.
            Based on the full interview transcript and scores provided,
            return a JSON object with exactly these fields:
            {
              "strengths": "<2-3 sentences about what the candidate did well>",
              "areasToImprove": "<2-3 sentences about what needs improvement>",
              "recommendation": "<one of: RECOMMENDED, MAYBE, NOT_RECOMMENDED>"
            }
            Return only valid JSON. No explanation, no markdown, no extra text.
            """;

        // build transcript summary for GPT
        String transcript = buildTranscriptSummary(questions, analysis);

        String userPrompt = String.format("""
            Interview Type: %s
            Overall Score: %.2f / 10
            
            Full Interview Transcript:
            %s
            
            Evaluate the candidate and return the JSON.
            """,
            session.getInterviewSetup().getInterviewType().name(),
            session.getOverallScore() != null ? session.getOverallScore() : 0.0,
            transcript
        );

        try {
            String gptResponse = openAIClient.chat(systemPrompt, userPrompt);
            JsonNode json = objectMapper.readTree(gptResponse);

            return new GPTEvaluationResult(
                    json.path("strengths").asText(),
                    json.path("areasToImprove").asText(),
                    json.path("recommendation").asText()
            );

        } catch (Exception e) {
            log.error("GPT evaluation failed: {}", e.getMessage());
            return new GPTEvaluationResult(
                    "Unable to generate strengths.",
                    "Unable to generate areas to improve.",
                    "MAYBE"
            );
        }
    }

    // builds a readable transcript for GPT
    private String buildTranscriptSummary(List<InterviewQuestionHistory> questions,
                                           List<AnalysisResult> analysisList) {
        StringBuilder sb = new StringBuilder();
        for (InterviewQuestionHistory q : questions) {
            sb.append("Q").append(q.getQuestionNumber()).append(": ").append(q.getQuestionText()).append("\n");
            if (q.isWasSkipped()) {
                sb.append("A: [Skipped]\n");
            } else {
                sb.append("A: ").append(q.getUserAnswer()).append("\n");
                // find matching analysis
                analysisList.stream()
                        .filter(a -> a.getQuestionHistory().getId().equals(q.getId()))
                        .findFirst()
                        .ifPresent(a -> sb.append("Score: ").append(a.getOverallScore())
                                .append(" | Feedback: ").append(a.getFeedbackSummary()).append("\n"));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // maps score to a performance band label
    private String getPerformanceBand(double score) {
        if (score >= 8.0) return "Excellent";
        if (score >= 6.0) return "Good";
        if (score >= 4.0) return "Average";
        if (score >= 2.0) return "Below Average";
        return "Poor";
    }

    // internal record to hold GPT evaluation result
    private record GPTEvaluationResult(String strengths, String areasToImprove, String recommendation) {}

    // ADD this method to your existing EvaluationService.java
    // This is used by PdfExportController to get the full DTO for PDF generation

    public EvaluationReportResponseDTO getReportDTO(String sessionToken) {
        EvaluationReport report = getReportBySessionToken(sessionToken);
        InterviewSession session = report.getInterviewSession();
        InterviewSetup setup = session.getInterviewSetup();

        List<InterviewQuestionHistory> questions = questionHistoryRepository
                .findByInterviewSessionIdOrderByQuestionNumberAsc(session.getId());

        List<EvaluationReportResponseDTO.QuestionBreakdownDTO> breakdown = questions.stream()
                .map(q -> {
                    AnalysisResult analysis = q.getAnalysisResult();
                    return EvaluationReportResponseDTO.QuestionBreakdownDTO.builder()
                            .questionNumber(q.getQuestionNumber())
                            .questionText(q.getQuestionText())
                            .userAnswer(q.getUserAnswer())
                            .wasSkipped(q.isWasSkipped())
                            .isFollowUp(q.isFollowUp())
                            .relevanceScore(analysis != null ? analysis.getRelevanceScore() : 0)
                            .conceptScore(analysis != null ? analysis.getConceptScore() : 0)
                            .clarityScore(analysis != null ? analysis.getClarityScore() : 0)
                            .grammarScore(analysis != null ? analysis.getGrammarScore() : 0)
                            .completenessScore(analysis != null ? analysis.getCompletenessScore() : 0)
                            .overallScore(analysis != null ? analysis.getOverallScore() : 0)
                            .feedbackSummary(analysis != null ? analysis.getFeedbackSummary() : "")
                            .responseTimeSeconds(q.getResponseTimeSeconds())
                            .build();
                })
                .collect(Collectors.toList());

        return EvaluationReportResponseDTO.builder()
                .reportId(report.getId())
                .sessionToken(session.getSessionToken())
                .interviewType(setup.getInterviewType().name())
                .techStack(setup.getTechStack())
                .interviewLevel(setup.getInterviewLevel() != null ? setup.getInterviewLevel().name() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .overallScore(report.getOverallScore())
                .performanceBand(report.getPerformanceBand())
                .strengths(report.getStrengths())
                .areasToImprove(report.getAreasToImprove())
                .recommendation(report.getRecommendation())
                .totalQuestions(report.getTotalQuestions())
                .answeredQuestions(report.getAnsweredQuestions())
                .skippedQuestions(report.getSkippedQuestions())
                .totalViolations(report.getTotalViolations())
                .questionBreakdown(breakdown)
                .generatedAt(report.getGeneratedAt())
                .build();
    }
}
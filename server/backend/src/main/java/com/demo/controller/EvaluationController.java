package com.demo.controller;

import com.demo.dto.EvaluationReportResponseDTO;
import com.demo.model.*;
import com.demo.repository.*;
import com.demo.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final UserRepository userRepository;
    private final InterviewQuestionHistoryRepository questionHistoryRepository;
    private final AnalysisResultRepository analysisResultRepository;

    // POST /api/evaluation/generate/{sessionToken}
    // called automatically when INTERVIEW_END is received on frontend
    @PostMapping("/generate/{sessionToken}")
    public ResponseEntity<EvaluationReportResponseDTO> generateReport(
            @PathVariable String sessionToken) {
        EvaluationReport report = evaluationService.generateReport(sessionToken);
        return ResponseEntity.ok(mapToDTO(report));
    }

    // GET /api/evaluation/{sessionToken}
    // fetch report for evaluation page
    @GetMapping("/{sessionToken}")
    public ResponseEntity<EvaluationReportResponseDTO> getReport(
            @PathVariable String sessionToken) {
        EvaluationReport report = evaluationService.getReportBySessionToken(sessionToken);
        return ResponseEntity.ok(mapToDTO(report));
    }

    // GET /api/evaluation/history
    // fetch all past reports for logged-in user (history page)
    @GetMapping("/history")
    public ResponseEntity<List<EvaluationReportResponseDTO>> getHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<EvaluationReportResponseDTO> history = evaluationService
                .getReportsByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    // map EvaluationReport entity to full response DTO with question breakdown
    private EvaluationReportResponseDTO mapToDTO(EvaluationReport report) {
        InterviewSession session = report.getInterviewSession();
        InterviewSetup setup = session.getInterviewSetup();

        // build per-question breakdown
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
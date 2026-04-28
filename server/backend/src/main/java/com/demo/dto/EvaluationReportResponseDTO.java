package com.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EvaluationReportResponseDTO {

    private Long reportId;
    private String sessionToken;

    // interview info
    private String interviewType;       // HR or TECHNICAL
    private List<String> techStack;     // null for HR
    private String interviewLevel;      // null for HR
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // scores
    private double overallScore;        // e.g. 7.4
    private String performanceBand;     // Excellent / Good / Average / Below Average / Poor

    // GPT feedback
    private String strengths;
    private String areasToImprove;
    private String recommendation;      // RECOMMENDED / MAYBE / NOT_RECOMMENDED

    // question summary
    private int totalQuestions;
    private int answeredQuestions;
    private int skippedQuestions;

    // proctoring summary
    private int totalViolations;

    // per-question breakdown (shown in detail section of evaluation page)
    private List<QuestionBreakdownDTO> questionBreakdown;

    private LocalDateTime generatedAt;

    @Data
    @Builder
    public static class QuestionBreakdownDTO {
        private int questionNumber;
        private String questionText;
        private String userAnswer;
        private boolean wasSkipped;
        private boolean isFollowUp;
        private int relevanceScore;
        private int conceptScore;
        private int clarityScore;
        private int grammarScore;
        private int completenessScore;
        private double overallScore;
        private String feedbackSummary;
        private Integer responseTimeSeconds;
    }
}
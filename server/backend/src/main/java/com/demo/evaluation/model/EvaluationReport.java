package com.demo.evaluation.model;

import com.demo.interview.model.InterviewSession;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // final overall score for the entire interview (avg of all question scores)
    private double overallScore;

    // e.g. "Strong", "Average", "Needs Improvement"
    private String performanceBand;

    // GPT generated overall strengths summary
    @Column(columnDefinition = "TEXT")
    private String strengths;

    // GPT generated areas to improve
    @Column(columnDefinition = "TEXT")
    private String areasToImprove;

    // final hiring recommendation: RECOMMENDED, MAYBE, NOT_RECOMMENDED
    private String recommendation;

    // number of questions answered vs skipped
    private int totalQuestions;
    private int answeredQuestions;
    private int skippedQuestions;

    // proctoring summary
    private int totalViolations;

    // when the report was generated
    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }

    // one report per session
    @OneToOne
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference
    private InterviewSession interviewSession;
}
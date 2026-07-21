package com.demo.evaluation.model;

import com.demo.interview.model.InterviewQuestionHistory;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "analysis_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Indicator scores (0 to 10) ---

    @Min(0) @Max(10)
    private int relevanceScore;   // how relevant was the answer to the question

    @Min(0) @Max(10)
    private int conceptScore;     // depth of technical/conceptual understanding

    @Min(0) @Max(10)
    private int clarityScore;     // how clearly the answer was communicated

    @Min(0) @Max(10)
    private int grammarScore;     // fixed typo: was "grammerScore"

    @Min(0) @Max(10)
    private int completenessScore; // how complete was the answer

    // weighted average of all indicator scores (computed by EvaluationService)
    private double overallScore;

    // GPT generated feedback for this specific answer
    @Column(columnDefinition = "TEXT")
    private String feedbackSummary;

    // if user skipped, all scores will be 0 and this flag will be true
    @Column(nullable = false)
    private boolean skipped = false;

    // linked question
    @OneToOne
    @JoinColumn(name = "question_history_id", nullable = false)
    @JsonBackReference
    private InterviewQuestionHistory questionHistory;
}
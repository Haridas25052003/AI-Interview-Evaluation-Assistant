package com.demo.model;

import jakarta.persistence.*;

@Entity
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int grammarScore;
    private int relevanceScore;
    private int clarityScore;
    private int conceptScore;

    private double overallScore;

    @Column(length = 1000)
    private String feedbackSummary;

    @OneToOne
    @JoinColumn(name = "question_id")
    private InterviewQuestion question;

    // Getters & Setters
}
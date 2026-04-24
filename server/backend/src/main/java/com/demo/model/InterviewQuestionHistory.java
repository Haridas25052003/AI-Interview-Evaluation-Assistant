package com.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_question_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String userAnswer; // null if skipped

    // 1-based index of this question in the session (1st, 2nd, 3rd...)
    @Column(nullable = false)
    private int questionNumber;

    // was this a follow-up to the previous answer, or a fresh random question?
    @Column(nullable = false)
    private boolean isFollowUp = false;

    // did the user skip this question (no answer given)?
    @Column(nullable = false)
    private boolean wasSkipped = false;

    // when was the question asked (TTS started)
    @Column(nullable = false)
    private LocalDateTime askedAt;

    // when did the user finish answering (or when silence was detected)
    private LocalDateTime answeredAt;

    // response time in seconds (answeredAt - askedAt), useful for evaluation
    private Integer responseTimeSeconds;

    @PrePersist
    protected void onCreate() {
        this.askedAt = LocalDateTime.now();
    }

    // which session this Q&A belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference
    private InterviewSession interviewSession;

    // one-to-one analysis result for this question
    @OneToOne(mappedBy = "questionHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AnalysisResult analysisResult;
}
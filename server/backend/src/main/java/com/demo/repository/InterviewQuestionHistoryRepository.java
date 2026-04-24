package com.demo.repository;

import com.demo.model.InterviewQuestionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewQuestionHistoryRepository extends JpaRepository<InterviewQuestionHistory, Long> {

    // get all questions for a session in order (used to build transcript)
    List<InterviewQuestionHistory> findByInterviewSessionIdOrderByQuestionNumberAsc(Long sessionId);

    // get the last answered question in a session (used to generate follow-up question)
    Optional<InterviewQuestionHistory> findTopByInterviewSessionIdOrderByQuestionNumberDesc(Long sessionId);

    // get a specific question by session and question number
    Optional<InterviewQuestionHistory> findByInterviewSessionIdAndQuestionNumber(Long sessionId, int questionNumber);

    // count how many questions have been asked so far in a session
    int countByInterviewSessionId(Long sessionId);

    // count skipped questions in a session (used in evaluation report)
    int countByInterviewSessionIdAndWasSkippedTrue(Long sessionId);

    // get all non-skipped questions (answered ones) for analysis
    @Query("SELECT q FROM InterviewQuestionHistory q WHERE q.interviewSession.id = :sessionId AND q.wasSkipped = false ORDER BY q.questionNumber ASC")
    List<InterviewQuestionHistory> findAnsweredQuestionsBySessionId(@Param("sessionId") Long sessionId);
}
package com.demo.repository;

import com.demo.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

	// get analysis for a specific question
	Optional<AnalysisResult> findByQuestionHistoryId(Long questionHistoryId);

	// get all analysis results for a session (used to compute final overall score)
	@Query("SELECT a FROM AnalysisResult a WHERE a.questionHistory.interviewSession.id = :sessionId")
	List<AnalysisResult> findAllBySessionId(@Param("sessionId") Long sessionId);

	// compute average overall score for a session (used in EvaluationReport)
	@Query("SELECT AVG(a.overallScore) FROM AnalysisResult a WHERE a.questionHistory.interviewSession.id = :sessionId AND a.skipped = false")
	Double computeAverageScoreBySessionId(@Param("sessionId") Long sessionId);
}
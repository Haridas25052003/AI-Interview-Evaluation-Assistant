package com.demo.repository;

import com.demo.model.EvaluationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationReportRepository extends JpaRepository<EvaluationReport, Long> {

    // get report for a specific session (used on evaluation page)
    Optional<EvaluationReport> findByInterviewSessionId(Long sessionId);

    // get all reports for a user — used on profile/history page
    @Query("SELECT e FROM EvaluationReport e WHERE e.interviewSession.interviewSetup.user.id = :userId ORDER BY e.generatedAt DESC")
    List<EvaluationReport> findAllByUserId(@Param("userId") Long userId);

    // check if report already exists for a session (avoid duplicate generation)
    boolean existsByInterviewSessionId(Long sessionId);
}
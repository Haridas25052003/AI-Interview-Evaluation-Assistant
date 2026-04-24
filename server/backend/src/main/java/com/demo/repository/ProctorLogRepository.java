package com.demo.repository;

import com.demo.model.ProctorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProctorLogRepository extends JpaRepository<ProctorLog, Long> {

    // get all violations for a session (used in evaluation report)
    List<ProctorLog> findByInterviewSessionIdOrderByDetectedAtAsc(Long sessionId);

    // count total violations for a session
    int countByInterviewSessionId(Long sessionId);

    // count violations by type (e.g. how many TAB_SWITCH vs FACE_NOT_DETECTED)
    int countByInterviewSessionIdAndViolationType(Long sessionId, String violationType);

    // check if a session has any violations at all
    boolean existsByInterviewSessionId(Long sessionId);

    // get all violation types summary for a session (used in evaluation report)
    @Query("SELECT p.violationType, COUNT(p) FROM ProctorLog p WHERE p.interviewSession.id = :sessionId GROUP BY p.violationType")
    List<Object[]> getViolationSummaryBySessionId(@Param("sessionId") Long sessionId);
}
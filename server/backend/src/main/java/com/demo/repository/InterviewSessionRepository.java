package com.demo.repository;

import com.demo.model.InterviewSession;
import com.demo.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    // find session by unique WebSocket token (used during live interview)
    Optional<InterviewSession> findBySessionToken(String sessionToken);

    // get all sessions for a specific setup
    List<InterviewSession> findByInterviewSetupId(Long setupId);

    // get all sessions for a user (across all their setups) — used for history page
    @Query("SELECT s FROM InterviewSession s WHERE s.interviewSetup.user.id = :userId ORDER BY s.startTime DESC")
    List<InterviewSession> findAllByUserId(@Param("userId") Long userId);

    // get all completed sessions for a user — used for evaluation history
    @Query("SELECT s FROM InterviewSession s WHERE s.interviewSetup.user.id = :userId AND s.status = :status ORDER BY s.startTime DESC")
    List<InterviewSession> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") SessionStatus status);

    // check if a session with this token is still in progress (used in WebSocket handler)
    boolean existsBySessionTokenAndStatus(String sessionToken, SessionStatus status);
}
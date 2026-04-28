package com.demo.service;

import com.demo.model.InterviewSession;
import com.demo.model.InterviewSetup;
import com.demo.model.SessionStatus;
import com.demo.repository.InterviewSessionRepository;
import com.demo.repository.InterviewSetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewSetupRepository interviewSetupRepository;

    // called when user clicks "Start Interview" on frontend
    // creates a new session and returns the sessionToken for WebSocket connection
    public InterviewSession startSession(Long setupId) {
        InterviewSetup setup = interviewSetupRepository.findById(setupId)
                .orElseThrow(() -> new RuntimeException("Setup not found: " + setupId));

        InterviewSession session = InterviewSession.builder()
                .interviewSetup(setup)
                .status(SessionStatus.IN_PROGRESS)
                .build();
        // sessionToken and startTime are set automatically via @PrePersist

        return interviewSessionRepository.save(session);
    }

    // called when all questions are done
    public InterviewSession completeSession(String sessionToken, double overallScore) {
        InterviewSession session = getSessionByToken(sessionToken);
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        session.setOverallScore(overallScore);
        return interviewSessionRepository.save(session);
    }

    // called if user closes browser or disconnects mid-interview
    public InterviewSession abandonSession(String sessionToken) {
        InterviewSession session = getSessionByToken(sessionToken);
        if (session.getStatus() == SessionStatus.IN_PROGRESS) {
            session.setStatus(SessionStatus.ABANDONED);
            session.setEndTime(LocalDateTime.now());
            interviewSessionRepository.save(session);
        }
        return session;
    }

    // increment violation count and save
    public void recordViolation(String sessionToken) {
        InterviewSession session = getSessionByToken(sessionToken);
        session.setProctorViolationCount(session.getProctorViolationCount() + 1);
        interviewSessionRepository.save(session);
    }

    public InterviewSession getSessionByToken(String sessionToken) {
        return interviewSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionToken));
    }
}
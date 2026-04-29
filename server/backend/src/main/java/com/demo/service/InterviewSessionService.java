package com.demo.service;

import com.demo.model.InterviewSession;
import com.demo.model.InterviewSetup;
import com.demo.model.SessionStatus;
import com.demo.repository.InterviewSessionRepository;
import com.demo.repository.InterviewSetupRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewSetupRepository interviewSetupRepository;

    // called when user clicks "Start Interview" on frontend
    // creates a new session and returns the sessionToken for WebSocket connection
    @Transactional
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
    @Transactional
    public InterviewSession completeSession(String sessionToken, double overallScore) {
        InterviewSession session = getSessionByToken(sessionToken);
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        session.setOverallScore(overallScore);
        return interviewSessionRepository.save(session);
    }

    // called if user closes browser or disconnects mid-interview
    @Transactional
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
    @Transactional
    public void recordViolation(String sessionToken) {
        InterviewSession session = getSessionByToken(sessionToken);
        session.setProctorViolationCount(session.getProctorViolationCount() + 1);
        interviewSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public InterviewSession getSessionByToken(String sessionToken) {
        InterviewSession session = interviewSessionRepository
                .findBySessionTokenWithSetup(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionToken));

        // Force initialization of lazy collections that will be needed in WebSocket handler
        InterviewSetup setup = session.getInterviewSetup();
        Hibernate.initialize(setup.getTechStack());

        return session;
    }
}
package com.demo.service;

import com.demo.dto.*;
import com.demo.model.*;
import com.demo.repository.InterviewQuestionHistoryRepository;
import com.demo.repository.ProctorLogRepository;
import com.demo.service.InterviewSessionService;
import com.demo.service.QuestionGeneratorService;
import com.demo.service.AnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewWebSocketHandler extends TextWebSocketHandler {

    private final InterviewSessionService sessionService;
    private final QuestionGeneratorService questionGeneratorService;
    private final AnalysisService analysisService;
    private final InterviewQuestionHistoryRepository questionHistoryRepository;
    private final ProctorLogRepository proctorLogRepository;
    private final ObjectMapper objectMapper;

    // keeps track of active WebSocket connections: sessionToken -> WebSocketSession
    // ConcurrentHashMap is thread-safe — important for concurrent interviews
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------
    // 1. CONNECTION ESTABLISHED
    // -----------------------------------------------------------------------
    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
        String sessionToken = extractSessionToken(wsSession);
        log.info("WebSocket connected: {}", sessionToken);

        // validate session exists and is IN_PROGRESS
        InterviewSession interviewSession = sessionService.getSessionByToken(sessionToken);
        if (interviewSession.getStatus() != SessionStatus.IN_PROGRESS) {
            wsSession.close(CloseStatus.BAD_DATA.withReason("Session is not active"));
            return;
        }

        // store the connection
        activeSessions.put(sessionToken, wsSession);

        // send the first question immediately
        sendNextQuestion(wsSession, interviewSession, null);
    }

    // -----------------------------------------------------------------------
    // 2. MESSAGE RECEIVED FROM FRONTEND
    // -----------------------------------------------------------------------
    @Override
    protected void handleTextMessage(WebSocketSession wsSession, TextMessage message) throws Exception {
        String sessionToken = extractSessionToken(wsSession);
        InterviewSession interviewSession = sessionService.getSessionByToken(sessionToken);

        // parse the incoming message
        WebSocketMessage wsMessage = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);

        switch (wsMessage.getType()) {

            case "ANSWER" -> {
                AnswerPayload answer = objectMapper.convertValue(wsMessage.getPayload(), AnswerPayload.class);
                handleAnswer(wsSession, interviewSession, answer);
            }

            case "SKIP" -> {
                AnswerPayload skip = objectMapper.convertValue(wsMessage.getPayload(), AnswerPayload.class);
                skip.setSkipped(true);
                skip.setAnswerText("");
                handleAnswer(wsSession, interviewSession, skip);
            }

            case "PROCTOR_EVENT" -> {
                ProctorEventPayload event = objectMapper.convertValue(wsMessage.getPayload(), ProctorEventPayload.class);
                handleProctorEvent(sessionToken, interviewSession, event);
            }

            default -> log.warn("Unknown message type: {}", wsMessage.getType());
        }
    }

    // -----------------------------------------------------------------------
    // 3. HANDLE ANSWER OR SKIP
    // -----------------------------------------------------------------------
    private void handleAnswer(WebSocketSession wsSession,
                               InterviewSession interviewSession,
                               AnswerPayload answer) throws Exception {

        InterviewSetup setup = interviewSession.getInterviewSetup();
        int currentQuestionNumber = answer.getQuestionNumber();

        // find the question record that was asked
        InterviewQuestionHistory questionHistory = questionHistoryRepository
                .findByInterviewSessionIdAndQuestionNumber(interviewSession.getId(), currentQuestionNumber)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // save the answer
        questionHistory.setUserAnswer(answer.isSkipped() ? null : answer.getAnswerText());
        questionHistory.setWasSkipped(answer.isSkipped());
        questionHistory.setAnsweredAt(LocalDateTime.now());

        // calculate response time in seconds
        if (questionHistory.getAskedAt() != null) {
            long seconds = java.time.Duration.between(
                questionHistory.getAskedAt(), questionHistory.getAnsweredAt()).getSeconds();
            questionHistory.setResponseTimeSeconds((int) seconds);
        }

        questionHistoryRepository.save(questionHistory);

        // analyze the answer via GPT (skip analysis if answer was skipped)
        AnalysisResult analysis = analysisService.analyzeAnswer(questionHistory);

        // send analysis result back to frontend
        sendMessage(wsSession, WebSocketMessage.builder()
                .type("ANALYSIS")
                .payload(analysis)
                .build());

        // check if interview is complete
        int answeredCount = questionHistoryRepository.countByInterviewSessionId(interviewSession.getId());
        if (answeredCount >= setup.getTotalQuestions()) {
            // all questions done — end the interview
            endInterview(wsSession, interviewSession);
        } else {
            // send next question
            // if user answered (not skipped) → follow-up question
            // if user skipped → random question
            sendNextQuestion(wsSession, interviewSession, answer.isSkipped() ? null : questionHistory);
        }
    }

    // -----------------------------------------------------------------------
    // 4. SEND NEXT QUESTION
    // -----------------------------------------------------------------------
    private void sendNextQuestion(WebSocketSession wsSession,
                                   InterviewSession interviewSession,
                                   InterviewQuestionHistory lastAnswer) throws Exception {

        InterviewSetup setup = interviewSession.getInterviewSetup();
        int nextQuestionNumber = questionHistoryRepository
                .countByInterviewSessionId(interviewSession.getId()) + 1;

        boolean isFollowUp = lastAnswer != null;

        // generate question via GPT
        String questionText = questionGeneratorService.generateQuestion(
                setup,
                lastAnswer,
                isFollowUp
        );

        // save the question to DB
        InterviewQuestionHistory questionHistory = InterviewQuestionHistory.builder()
                .questionText(questionText)
                .questionNumber(nextQuestionNumber)
                .isFollowUp(isFollowUp)
                .wasSkipped(false)
                .interviewSession(interviewSession)
                .build();
        // askedAt is set automatically via @PrePersist

        questionHistoryRepository.save(questionHistory);

        // send to frontend
        sendMessage(wsSession, WebSocketMessage.builder()
                .type("QUESTION")
                .payload(QuestionPayload.builder()
                        .questionNumber(nextQuestionNumber)
                        .totalQuestions(setup.getTotalQuestions())
                        .questionText(questionText)
                        .isFollowUp(isFollowUp)
                        .build())
                .build());
    }

    // -----------------------------------------------------------------------
    // 5. HANDLE PROCTORING VIOLATION
    // -----------------------------------------------------------------------
    private void handleProctorEvent(String sessionToken,
                                     InterviewSession interviewSession,
                                     ProctorEventPayload event) {
        // log violation to DB
        ProctorLog log = ProctorLog.builder()
                .violationType(event.getViolationType())
                .details(event.getDetails())
                .interviewSession(interviewSession)
                .build();
        proctorLogRepository.save(log);

        // increment violation counter on session
        sessionService.recordViolation(sessionToken);

        log.info("Proctor violation [{}]: {} - {}", sessionToken, event.getViolationType(), event.getDetails());
    }

    // -----------------------------------------------------------------------
    // 6. END INTERVIEW
    // -----------------------------------------------------------------------
    private void endInterview(WebSocketSession wsSession, InterviewSession interviewSession) throws Exception {
        // compute final score from all analysis results
        double finalScore = analysisService.computeFinalScore(interviewSession.getId());

        // mark session as completed
        sessionService.completeSession(interviewSession.getSessionToken(), finalScore);

        // notify frontend — it will redirect to evaluation page
        sendMessage(wsSession, WebSocketMessage.builder()
                .type("INTERVIEW_END")
                .payload(Map.of(
                        "sessionToken", interviewSession.getSessionToken(),
                        "overallScore", finalScore,
                        "message", "Interview completed successfully!"
                ))
                .build());
    }

    // -----------------------------------------------------------------------
    // 7. CONNECTION CLOSED
    // -----------------------------------------------------------------------
    @Override
    public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus status) throws Exception {
        String sessionToken = extractSessionToken(wsSession);
        activeSessions.remove(sessionToken);

        // if browser closed mid-interview, mark as abandoned
        sessionService.abandonSession(sessionToken);
        log.info("WebSocket disconnected: {} | status: {}", sessionToken, status);
    }

    // -----------------------------------------------------------------------
    // HELPERS
    // -----------------------------------------------------------------------

    // send a message to a specific WebSocket session
    private void sendMessage(WebSocketSession wsSession, WebSocketMessage message) throws Exception {
        String json = objectMapper.writeValueAsString(message);
        wsSession.sendMessage(new TextMessage(json));
    }

    // extract sessionToken from WebSocket URL: /ws/interview/{sessionToken}
    private String extractSessionToken(WebSocketSession wsSession) {
        String path = wsSession.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
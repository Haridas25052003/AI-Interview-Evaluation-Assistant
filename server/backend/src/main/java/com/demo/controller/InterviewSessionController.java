package com.demo.controller;

import com.demo.model.InterviewSession;
import com.demo.service.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interview/session")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    // POST /api/interview/session/start?setupId=1
    // called by frontend BEFORE opening WebSocket connection
    // returns sessionToken which is used as: ws://localhost:8080/ws/interview/{sessionToken}
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startSession(@RequestParam Long setupId) {
        InterviewSession session = interviewSessionService.startSession(setupId);
        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "sessionToken", session.getSessionToken(),
                "status", session.getStatus(),
                "startTime", session.getStartTime()
        ));
    }
}
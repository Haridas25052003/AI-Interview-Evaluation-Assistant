package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    // message types sent FROM server TO frontend:
    // "QUESTION"       — next question to ask
    // "ANALYSIS"       — analysis result after each answer
    // "INTERVIEW_END"  — interview is complete
    // "ERROR"          — something went wrong

    // message types sent FROM frontend TO server:
    // "ANSWER"         — user's spoken answer (transcribed text)
    // "SKIP"           — user skipped the question
    // "PROCTOR_EVENT"  — proctoring violation detected

    private String type;    // message type (see above)
    private Object payload; // actual data — cast based on type
}
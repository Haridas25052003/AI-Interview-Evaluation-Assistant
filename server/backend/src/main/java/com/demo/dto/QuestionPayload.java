package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPayload {

    private int questionNumber;   // e.g. 1, 2, 3...
    private int totalQuestions;   // so frontend can show "Question 2 of 10"
    private String questionText;  // the actual question to speak via TTS
    private boolean isFollowUp;   // so frontend knows context
}
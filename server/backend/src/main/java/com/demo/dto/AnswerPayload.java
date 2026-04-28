package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerPayload {

    private int questionNumber; // which question this answer belongs to
    private String answerText;  // transcribed speech text (empty if skipped)
    private boolean skipped;    // true if user did not answer
}
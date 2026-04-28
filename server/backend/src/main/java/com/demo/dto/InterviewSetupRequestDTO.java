package com.demo.dto;

import com.demo.model.InterviewLevel;
import com.demo.model.InterviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InterviewSetupRequestDTO {

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType; // HR or TECHNICAL

    // --- below fields only required if interviewType = TECHNICAL ---

    private List<String> techStack; // e.g. ["Java", "Spring Boot", "React"]

    private InterviewLevel interviewLevel; // BEGINNER, INTERMEDIATE, EXPERT

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience seems too high")
    private Integer yearsOfExperience;

    @Min(value = 1, message = "At least 1 question required")
    @Max(value = 20, message = "Maximum 20 questions allowed")
    private int totalQuestions = 10; // default 10
}
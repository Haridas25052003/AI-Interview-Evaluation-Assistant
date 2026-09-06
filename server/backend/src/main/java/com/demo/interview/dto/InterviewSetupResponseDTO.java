package com.demo.interview.dto;

import com.demo.interview.model.InterviewLevel;
import com.demo.interview.model.InterviewType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InterviewSetupResponseDTO {

    private Long id;
    private InterviewType interviewType;
    private List<String> techStack;
    private InterviewLevel interviewLevel;
    private Integer yearsOfExperience;
    private int totalQuestions;
    private Long userId;
    private String userName;
}
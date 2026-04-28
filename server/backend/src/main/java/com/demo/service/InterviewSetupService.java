package com.demo.service;

import com.demo.dto.InterviewSetupRequestDTO;
import com.demo.dto.InterviewSetupResponseDTO;
import com.demo.model.InterviewSetup;
import com.demo.model.InterviewType;
import com.demo.model.User;
import com.demo.repository.InterviewSetupRepository;
import com.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewSetupService {

    private final InterviewSetupRepository interviewSetupRepository;
    private final UserRepository userRepository;

    // create a new interview setup for the logged-in user
    public InterviewSetupResponseDTO createSetup(InterviewSetupRequestDTO request) {

        User user = getLoggedInUser();

        // validate: if TECHNICAL, techStack and level must be provided
        if (request.getInterviewType() == InterviewType.TECHNICAL) {
            if (request.getTechStack() == null || request.getTechStack().isEmpty()) {
                throw new RuntimeException("Tech stack is required for Technical interviews");
            }
            if (request.getInterviewLevel() == null) {
                throw new RuntimeException("Interview level is required for Technical interviews");
            }
        }

        InterviewSetup setup = InterviewSetup.builder()
                .interviewType(request.getInterviewType())
                .techStack(request.getTechStack())
                .interviewLevel(request.getInterviewLevel())
                .yearsOfExperience(request.getYearsOfExperience())
                .totalQuestions(request.getTotalQuestions())
                .user(user)
                .build();

        InterviewSetup saved = interviewSetupRepository.save(setup);
        return mapToResponse(saved);
    }

    // get all setups for the logged-in user (shown on profile page)
    public List<InterviewSetupResponseDTO> getMySetups() {
        User user = getLoggedInUser();
        return interviewSetupRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // get a single setup by id
    public InterviewSetupResponseDTO getSetupById(Long setupId) {
        InterviewSetup setup = interviewSetupRepository.findById(setupId)
                .orElseThrow(() -> new RuntimeException("Setup not found with id: " + setupId));
        return mapToResponse(setup);
    }

    // get the currently logged-in user from SecurityContext
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    // map entity to response DTO
    private InterviewSetupResponseDTO mapToResponse(InterviewSetup setup) {
        return InterviewSetupResponseDTO.builder()
                .id(setup.getId())
                .interviewType(setup.getInterviewType())
                .techStack(setup.getTechStack())
                .interviewLevel(setup.getInterviewLevel())
                .yearsOfExperience(setup.getYearsOfExperience())
                .totalQuestions(setup.getTotalQuestions())
                .userId(setup.getUser().getId())
                .userName(setup.getUser().getName())
                .build();
    }
}
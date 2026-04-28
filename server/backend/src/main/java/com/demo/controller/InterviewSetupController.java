package com.demo.controller;

import com.demo.dto.InterviewSetupRequestDTO;
import com.demo.dto.InterviewSetupResponseDTO;
import com.demo.service.InterviewSetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview/setup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InterviewSetupController {

    private final InterviewSetupService interviewSetupService;

    // POST /api/interview/setup
    // creates a new interview setup (HR or Technical)
    // requires: Authorization: Bearer <token>
    // Body (HR):         { "interviewType": "HR", "totalQuestions": 10 }
    // Body (Technical):  { "interviewType": "TECHNICAL", "techStack": ["Java", "Spring Boot"],
    //                      "interviewLevel": "INTERMEDIATE", "yearsOfExperience": 2, "totalQuestions": 10 }
    @PostMapping
    public ResponseEntity<InterviewSetupResponseDTO> createSetup(
            @Valid @RequestBody InterviewSetupRequestDTO request) {
        InterviewSetupResponseDTO response = interviewSetupService.createSetup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/interview/setup
    // get all setups for the logged-in user
    @GetMapping
    public ResponseEntity<List<InterviewSetupResponseDTO>> getMySetups() {
        return ResponseEntity.ok(interviewSetupService.getMySetups());
    }

    // GET /api/interview/setup/{id}
    // get a specific setup by id
    @GetMapping("/{id}")
    public ResponseEntity<InterviewSetupResponseDTO> getSetupById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewSetupService.getSetupById(id));
    }
}
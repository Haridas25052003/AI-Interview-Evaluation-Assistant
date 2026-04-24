package com.demo.repository;

import com.demo.model.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.InterviewSetup;

import java.util.List;

public interface InterviewSetupRepository extends JpaRepository<InterviewSetup,Long>{

    // get all interview setups for a specific user (shown on profile/history page)
    List<InterviewSetup> findByUserId(Long userId);

    // get all HR setups for a user
    List<InterviewSetup> findByUserIdAndInterviewType(Long userId, InterviewType interviewType);

    // check if a user has any setup at all
    boolean existsByUserId(Long userId);

}

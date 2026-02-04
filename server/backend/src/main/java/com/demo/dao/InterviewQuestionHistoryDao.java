package com.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.InterviewQuestionHistory;

public interface InterviewQuestionHistoryDao extends JpaRepository<InterviewQuestionHistory,Integer>{

}

package com.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.InterviewQuestion;

public interface InterviewQuestionHistoryDao extends JpaRepository<InterviewQuestion,Integer>{

}

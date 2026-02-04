package com.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.InterviewSession;

public interface InterviewSessionDao extends JpaRepository<InterviewSession,Integer>{

}

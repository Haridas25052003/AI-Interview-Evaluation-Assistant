package com.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.AnalysisResult;

public interface AnalysisResultDao extends JpaRepository<AnalysisResult,Integer>{

}

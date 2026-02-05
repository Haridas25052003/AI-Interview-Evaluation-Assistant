package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.InterviewSetupDao;
import com.demo.model.InterviewSetup;

@Service
public class InterviewSetupServiceImpl implements InterviewSetupService{

	@Autowired
	private InterviewSetupDao id;

	@Override
	public InterviewSetup saveSetup(InterviewSetup setup) {
		
		return id.save(setup);
	}
	
	
}

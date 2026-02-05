package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.UserDao;
import com.demo.model.User;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao ud;

	@Override
	public User registerUser(User user) {
		
		return ud.save(user);
	}

	@Override
	public User loginUser(String email, String password) {
		
		User user=ud.findByEmail(email);
		if(user!=null && user.getPassword().equals(password)) {
			return user;
		}
		return null;
	}
}

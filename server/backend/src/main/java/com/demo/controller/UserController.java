package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.User;
import com.demo.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService us;
	
	//register user
	@PostMapping("/register")
	public User m1(@RequestBody User user) {
		return us.registerUser(user);
	}
	
	//login user
	@PostMapping("/login")
	public User m2(@RequestParam String email,
			@RequestParam String password) {
		return us.loginUser(email, password);
	}
	                                           

}

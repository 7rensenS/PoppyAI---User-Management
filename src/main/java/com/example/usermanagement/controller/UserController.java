package com.example.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody User user) {
		User registeredUser = userService.registerUser(user);
		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/validate")
	public ResponseEntity<User> validateUser(@RequestParam String email, @RequestParam String password) {
		User user = userService.validateUser(email, password);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	// ... other endpoints ...
}

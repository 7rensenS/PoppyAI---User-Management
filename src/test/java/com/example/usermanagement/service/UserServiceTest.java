package com.example.usermanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void registerUser_success() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setName("Test User");
		user.setGender("Male");

		when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		User registeredUser = userService.registerUser(user);

		assertNotNull(registeredUser);
		assertEquals(user.getEmail(), registeredUser.getEmail());
	}

}

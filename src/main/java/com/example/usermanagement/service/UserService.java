package com.example.usermanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.usermanagement.exception.UserAlreadyExistsException;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder; // For password hashing

	private final RestTemplate restTemplate = new RestTemplate();

	public User registerUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new UserAlreadyExistsException("Email already exists");
		}

		String ipAddress = getIpAddress();
		String country = getCountry(ipAddress);

		user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
		user.setIpAddress(ipAddress);
		user.setCountry(country);
		return userRepository.save(user);
	}

	public User validateUser(String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user != null && passwordEncoder.matches(password, user.getPassword())) { // Use matches() for password check
			return user;
		}
		return null; // Or throw an exception
	}

	// ... other methods (getAllUsers, deleteUser) ...

	private String getIpAddress() {
		try {
			return restTemplate.getForObject("https://api.ipify.org?format=json", String.class);
		} catch (Exception e) {
			return "Could not fetch IP Address";
		}
	}

	private String getCountry(String ipAddress) {
		try {
			String apiUrl = "http://ip-api.com/#" + ipAddress;
			return restTemplate.getForObject(apiUrl, String.class);

		} catch (Exception e) {
			return "Could not fetch Country";
		}
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public void deleteUser(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			userRepository.delete(user);
		}
	}

}
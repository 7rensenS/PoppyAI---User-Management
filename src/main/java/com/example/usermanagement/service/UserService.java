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

	private String getIpAddress() {
		try {
			JsonResponse jsonResponse = restTemplate.getForObject("https://api.ipify.org?format=json",
					JsonResponse.class);
			if (jsonResponse != null && jsonResponse.getIp() != null) {
				return jsonResponse.getIp();
			} else {
				return "Could not fetch IP";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Could not fetch IP";
		}
	}

	private String getCountry(String ipAddress) {
		try {
			String apiUrl = "http://ip-api.com/json/" + ipAddress;
			IpApiResponse response = restTemplate.getForObject(apiUrl, IpApiResponse.class);

			if (response != null && "success".equals(response.getStatus())) {
				return response.getCountry();
			} else {

				if (response != null && "fail".equals(response.getStatus())) {
					return "Error: " + response.getMessage();
				}
				return "Error: Could not retrieve country information. Invalid IP or API issue.";
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: An error occurred: " + e.getMessage());
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
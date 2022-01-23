package com.cts.auth.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cts.auth.model.User;

import lombok.extern.slf4j.Slf4j;


@SpringBootTest
@Slf4j
class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("Method to test findById() method when user present in database")
	void validate_existingUser() {
		log.info("TEST EXECUTION START - testFindUserById_userExists()");
		final String username = "admin";
		Optional<User> userOptional = userRepository.findById(username);
		assertTrue(userOptional.isPresent());
		assertEquals(username, userOptional.get().getUsername());
		log.info("END - testFindUserById_userExists()");
	}

	@Test
	@DisplayName("Method to test findById() method when user not present in database")
	void validate_notExistingUser() {
		log.info("TEST EXECUTION START - validate_notExistingUser()");
		final String id = "admin1";
		Optional<User> userOptional = userRepository.findById(id);
		assertTrue(userOptional.isEmpty());
		log.info("TEST EXECUTION END - validate_notExistingUser()");
	}
}

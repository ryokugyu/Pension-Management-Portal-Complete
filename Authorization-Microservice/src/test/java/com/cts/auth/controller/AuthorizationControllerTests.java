package com.cts.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.cts.auth.exception.InvalidTokenException;
import com.cts.auth.model.UserRequest;
import com.cts.auth.service.UserServiceImpl;
import com.cts.auth.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest
@Slf4j
class AuthorizationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserServiceImpl userServiceImpl;

	@MockBean
	private JWTUtil jwtUtil;

	@MockBean
	private AuthenticationManager authenticationManager;

	@Value("${userDetails.errorMessage}")
	private String ERROR_MESSAGE;

	@Value("${userDetails.badCredentialsMessage}")
	private String BAD_CREDENTIALS_MESSAGE;

	@Value("${userDetails.disabledAccountMessage}")
	private String DISABLED_ACCOUNT_MESSAGE;

	@Value("${userDetails.lockedAccountMessage}")
	private String LOCKED_ACCOUNT_MESSAGE;

	private static ObjectMapper mapper = new ObjectMapper();
	private static SecurityUser validUser;
	private static SecurityUser invalidUser;

	@BeforeEach
	void generateUserCredentials() {
		// User object
		validUser = new SecurityUser("admin", "$2a$10$aMMcsBB18R7dqzC7Wcg3z.oiVQnNhgFGD0WMTZVeVtFCMMnru25AO",
				Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));
		invalidUser = new SecurityUser("admin1", "$2a$10$aMMcsBB18R7dqzC7Wcg3z.oiVQnNhgFGD0WMTZVeVtFCMMnru25AO",
				Collections.singletonList(new SimpleGrantedAuthority("USER")));
	}

	/*****************************************************************
	 * User Login Tests
	 * 
	 * @throws Exception
	 * 
	 *****************************************************************
	 */
	@Test
	@DisplayName("Method to validate authenticateTheRequest() method with valid credentials")
	void authenticateTheRequest_validCredentials() throws Exception {
		log.info("TEST EXECUTION START - authenticateTheRequest_validCredentials()");

		// Set the user request
		UserRequest user = new UserRequest("admin", "admin");

		String token = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjcwMzk2NzcsInN1YiI6ImFkbWluMSIsImV4cCI6MTY1ODU3NTY3N30.trkCUngtLG8C1W6obvcGvQhCK1J9qg2Hsbcn8GJB95Y";
		log.info("Token: {}", token);

		// mock certain functionalities to return a valid user and generate the token
		when(authenticationManager.authenticate(ArgumentMatchers.any()))
				.thenReturn(new TestingAuthenticationToken("admin", "admin", "ADMIN"));
		when(jwtUtil.generateToken(ArgumentMatchers.any())).thenReturn(token);

		String json = mapper.writeValueAsString(user);
		log.info("Input data {}", json);

		MvcResult result = mockMvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8").content(json).accept(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
				.andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		assertNotNull(contentAsString);
		// match the token from the response body
		assertEquals(contentAsString, token);

		log.info("TEST EXECUTION END - authenticateTheRequest_validCredentials()");
	}

	@Test
	@DisplayName("Method to validate authenticateTheRequest() method with invalid account credentials")
	void authenticateTheRequest_invalidCredentials() throws Exception {
		log.info("TEST EXECUTION START - authenticateTheRequest_invalidCredentials()");

		// Set the user request and role
		UserRequest user = new UserRequest("admin1", "admin1");

		// mock certain functionalities to return a valid user and generate the token
		when(authenticationManager.authenticate(ArgumentMatchers.any()))
				.thenThrow(new BadCredentialsException("Invalid Username or Password. Please enter correct username or password."));

		String json = mapper.writeValueAsString(user);
		log.info("Input data {}", json);

		mockMvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(json)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", Matchers.equalTo("Invalid Username or Password. Please enter correct username or password.")));

		log.info("TEST EXECUTION END - authenticateTheRequest_invalidCredentials()");
	}

	@Test
	@DisplayName("Method to validate authenticateTheRequest() method with locked account credentials")
	void authenticateTheRequest_LockedCredentials() throws Exception {
		log.info("TEST EXECUTION START - authenticateTheRequest_LockedCredentials()");

		// Set the user request and role
		UserRequest user = new UserRequest("admin1", "admin1");

		// mock certain functionalities to return a valid user and generate the token
		when(authenticationManager.authenticate(ArgumentMatchers.any())).thenThrow(new LockedException(
				"Your account has been locked. Please contact your administrator for more details."));

		String json = mapper.writeValueAsString(user);
		log.info("Input data {}", json);

		mockMvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.content(json).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", Matchers
						.equalTo("Your account has been locked. Please contact your administrator for more details.")));

		log.info("TEST EXECUTION END - authenticateTheRequest_LockedCredentials()");
	}

	@Test
	@DisplayName("Method to validate authenticateTheRequest() method with disabled account credentials")
	void authenticateTheRequest_DisabledAccountCredentials() throws Exception {
		log.info("TEST EXECUTION START - testLogin_withDisabledAccountCredentials()");

		// Set the user request and role
		UserRequest user = new UserRequest("admin1", "admin1");

		// mock certain functionalities to return a valid user and generate the token
		when(authenticationManager.authenticate(ArgumentMatchers.any())).thenThrow(new DisabledException(
				"Your account has been disabled. Please contact your administrator for more details."));

		String json = mapper.writeValueAsString(user);
		log.info("Input data {}", json);

		mockMvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.content(json).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", Matchers.equalTo(
						"Your account has been disabled. Please contact your administrator for more details.")));

		log.info("TEST EXECUTION END - authenticateTheRequest_DisabledAccountCredentials()");
	}

	/*****************************************************************
	 * Token Validation Tests
	 * 
	 * @throws Exception
	 * 
	 *****************************************************************
	 */
	@Test
	@DisplayName("Method to validate authorizeTheRequest() method with valid token")
	void testAuthorizeTheRequest_validToken() throws Exception {
		log.info("TEST EXECUTION START - testAuthorizeTheRequest_validToken()");

		// mock certain functionalities to load user and have a valid token
		when(userServiceImpl.loadUserByUsername(ArgumentMatchers.any())).thenReturn(validUser);
		when(jwtUtil.isTokenExpiredOrInvalidFormat(ArgumentMatchers.any())).thenReturn(false);

		// set the token
		String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjcwMzk2NzcsInN1YiI6ImFkbWluMSIsImV4cCI6MTY1ODU3NTY3N30.trkCUngtLG8C1W6obvcGvQhCK1J9qg2Hsbcn8GJB95Y";
		log.info("Token: {}", token);

		// perform the mock
		mockMvc.perform(get("/authorize").header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isOk());

		log.info("TEST EXECUTION END - testAuthorizeTheRequest_validToken()");
	}

	@Test
	@DisplayName("Method to validate authorizeTheRequest() method with invalid/expired token")
	void testAuthorizeTheRequest_invalidToken() throws Exception {
		log.info("TEST EXECUTION START - testAuthorizeTheRequest_invalidToken()");
		final String errorMessage = "TOKEN EXPIRED";

		// mock certain functionalities to load user and have a invalid token
		when(userServiceImpl.loadUserByUsername(ArgumentMatchers.any())).thenReturn(validUser);
		when(jwtUtil.isTokenExpiredOrInvalidFormat(ArgumentMatchers.any()))
				.thenThrow(new InvalidTokenException(errorMessage));

		// set the invalid token
		String token = "Bearer fyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjcwMzk2NzcsInN1YiI6ImFkbWluMSIsImV4cCI6MTY1ODU3NTY3N30.trkCUngtLG8C1W6obvcGvQhCK1J9qg2Hsbcn8GJB95Y";
		log.info("Token: {}", token);

		// perform the mock
		mockMvc.perform(get("/authorize").header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().isBadRequest());

		log.info("TEST EXECUTION END - testAuthorizeTheRequest_invalidToken()");
	}

	@Test
	@DisplayName("Method to validate authorizeTheRequest() method with invalid role")
	void testAuthorizeTheRequest_invalidRole() throws Exception {
		log.info("TEST EXECUTION START - testAuthorizeTheRequest_invalidRole()");

		// mock certain functionalities to load invalid user and have a valid token
		when(userServiceImpl.loadUserByUsername(ArgumentMatchers.any())).thenReturn(invalidUser);
		when(jwtUtil.isTokenExpiredOrInvalidFormat(ArgumentMatchers.any())).thenReturn(false);

		// set the invalid token
		String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjcwMzk2NzcsInN1YiI6ImFkbWluMSIsImV4cCI6MTY1ODU3NTY3N30.trkCUngtLG8C1W6obvcGvQhCK1J9qg2Hsbcn8GJB95Y";
		log.info("Token: {}", token);

		// perform the mock
		mockMvc.perform(get("/authorize").header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isUnauthorized());

		log.info("TEST EXECUTION END - testAuthorizeTheRequest_invalidRole()");
	}

	@Test
	@DisplayName("Method to validate controller health")
	void testHealthCheck() throws Exception {
		log.info("TEST EXECUTION START - testHealthCheck()");

		MvcResult result = mockMvc.perform(get("/health-check")).andExpect(status().is2xxSuccessful()).andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		assertEquals("Authorization Microservice - OK", contentAsString);
		assertNotNull(result);

		log.info("TEST EXECUTION END - testHealthCheck()");
	}

	public class SecurityUser extends org.springframework.security.core.userdetails.User {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
			super(username, password, authorities);
		}

	}

}

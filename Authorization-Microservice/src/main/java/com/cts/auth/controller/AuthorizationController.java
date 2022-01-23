package com.cts.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cts.auth.exception.InvalidCredentialsException;
import com.cts.auth.model.UserRequest;
import com.cts.auth.util.JWTUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Authorization Controller for authentication user details from DB and authorizing JWT Token
 * @controller - /authenticate /authorize
 * @encryption - bCrypt base 64
 */

@RestController
@Slf4j
@CrossOrigin
public class AuthorizationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private UserDetailsService userService;

	@PostMapping("/authenticate")
	public ResponseEntity<String> authenticateTheRequest(@RequestBody @Valid UserRequest userRequest)
			throws InvalidCredentialsException {
		log.info("EXECUTION START -> authenticateTheRequest()");
		try {
			Authentication authenticate = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
			if (authenticate.isAuthenticated()) {
				log.info("USER LOCATED SUCCESSFULLY -> Login() successful.");
			}
		} catch (BadCredentialsException e) {
			throw new InvalidCredentialsException(
					"Invalid Username or Password. Please enter correct username or password.");
		} catch (DisabledException e) {
			throw new InvalidCredentialsException(
					"Your account has been disabled. Please contact your administrator for more details.");
		} catch (LockedException e) {
			throw new InvalidCredentialsException(
					"Your account has been locked. Please contact your administrator for more details.");
		}

		String token = jwtUtil.generateToken(userRequest.getUsername());
		log.info("EXECUTION END -> authenticateTheRequest()");
		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	@GetMapping("/authorize")
	public ResponseEntity<Boolean> authorizeTheRequest(
			@RequestHeader(name = "Authorization", required = true) String requestTokenHeader) {
		log.info("EXECUTION START -> authorizeTheRequest()");

		jwtUtil.isTokenExpiredOrInvalidFormat(requestTokenHeader);

		String userName = jwtUtil.getUsernameFromToken(requestTokenHeader);
		UserDetails user = userService.loadUserByUsername(userName);
		if (user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			log.info("EXECUTION END -> authorizeTheRequest()");
			return new ResponseEntity<>(true, HttpStatus.OK);
		}

		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);

	}

	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("Authorization Microservice - OK", HttpStatus.OK);
	}
}

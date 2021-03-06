package com.cts.auth.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cts.auth.exception.InvalidTokenException;
import com.cts.auth.util.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class JWTUtilTests {

	@Autowired
	JWTUtil jwtUtil;

	@Test
	void jwtUtilNotNull() {
		assertNotNull(jwtUtil);
	}

	@Test
	@DisplayName("Method to test whether token is expired or not for valid token")
	void test_tokenExpired_validToken() {
		log.info("TEST EXECUTION START - test_tokenExpired_validToken()");

		// subject of our token
		final String username = "admin";

		// generate our token
		final String token = jwtUtil.generateToken(username);
		log.info("Token: {}", token);

		// Test the token validity
		assertFalse(jwtUtil.isTokenExpiredOrInvalidFormat(token));

		// Token should not be null
		assertNotNull(token);

		log.info("TEST EXECUTION END - test_tokenExpired_validToken()");
	}

	@Test
	@DisplayName("Method to test whether token is expired or not for expired Token")
	void test_tokenExpired_expiredToken() {
		log.info("TEST EXECUTION START - test_tokenExpired_expiredToken()");

		// generate our token
		final String token = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjcwMzE4MTgsInN1YiI6ImFkbWluMSIsImV4cCI6MTYyNzAzMTg3OH0.iBDf8UvcnHKa-TVHHxjOQUiC3oEVGgsYrJSvD5LhUQc";
		log.info("Expired Token: {}", token);

		// Test the token validity
		InvalidTokenException thrownException = assertThrows(InvalidTokenException.class,
				() -> jwtUtil.isTokenExpiredOrInvalidFormat(token));
		assertTrue(thrownException.getMessage().contains("TOKEN EXPIRED"));

		// Token should not be null
		assertNotNull(token);

		log.info("TEST EXECUTION END - test_tokenExpired_expiredToken()");
	}

	@Test
	@DisplayName("Method to test whether token is in Valid Format or not")
	void test_tokenExpired_invalidFormatToken() {
		log.info("TEST EXECUTION START - test_tokenExpired_invalidFormatToken()");

		// generate our token
		final String token = "eyJhbGOiJIUzI1NiJ9.eyJpYXQiOjE2MjcwMzE4MTgsInN1YiI6ImFkbWluMSIsImV4cCI6MTYyNzAzMTg3OH0.iBDf8UvcnHKa-TVHHxjOQUiC3oEVGgsYrJSvD5LhUQc";
		log.info("Malformed Token: {}", token);

		// Test the token validity
		InvalidTokenException thrownException = assertThrows(InvalidTokenException.class,
				() -> jwtUtil.isTokenExpiredOrInvalidFormat(token));
		assertTrue(thrownException.getMessage().contains("INVALID TOKEN FORMAT"));

		// Token should not be null
		assertNotNull(token);

		log.info("TEST EXECUTION END- test_tokenExpired_invalidFormatToken()");
	}

	@Test
	@DisplayName("Method to test whether token is  Null or not")
	void test_tokenExpired_nullToken() {
		log.info("TEST EXECUTION START - test_tokenExpired_nullToken()");

		// generate our token
		final String token = null;
		log.info("Null Token: {}", token);

		// Test the token validity
		InvalidTokenException thrownException = assertThrows(InvalidTokenException.class,
				() -> jwtUtil.isTokenExpiredOrInvalidFormat(token));
		assertTrue(thrownException.getMessage().contains("NULL/EMPTY TOKEN"));

		// Token should not be null
		assertNull(token);

		log.info("TEST EXECUTION END - test_tokenExpired_nullToken()");
	}

	@Test
	@DisplayName("Method to test whether token has invalid signature or not")
	void test_tokenExpired_invalidTokenSignature() {
		log.info("TEST EXECUTION START - test_tokenExpired_invalidTokenSignature()");

		// generate our token
		final String token = "eyJhbGciOiJIUzI1NiJ91.eyJpYXQiOjE2MjczMjA2ODIsInN1YiI6ImFkbWluMSIsImV4cCI6MTYyNzMyMDc0Mn0.tiQjNTsiLwo7Q2EyuJeV9p187jUZVr7PCTZMs9gvBgk";
		log.info("Invalid token signature Token: {}", token);

		// Test the token validity
		InvalidTokenException thrownException = assertThrows(InvalidTokenException.class,
				() -> jwtUtil.isTokenExpiredOrInvalidFormat(token));
		assertTrue(thrownException.getMessage().contains("INVALID TOKEN SIGNATURE"));

		// Token should not be null
		assertNotNull(token);

		log.info("TEST EXECUTION END - test_tokenExpired_invalidTokenSignature()");
	}

	@Test
	@DisplayName("Method to test GetUsernameFromToken()")
	void test_fetchUsernameFromToken() {
		log.info("TEST EXECUTION START - test_fetchUsernameFromToken()");

		// Set the username
		final String username = "admin1";

		// Generate the token
		String token = jwtUtil.generateToken(username);
		log.info("Token: {}", token);

		// Username should be equal
		assertEquals(username, jwtUtil.getUsernameFromToken(token));

		log.info("TEST EXECUTION END - test_fetchUsernameFromToken()");
	}

}

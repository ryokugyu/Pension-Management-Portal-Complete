package com.cts.auth.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionDetails response = new ExceptionDetails();
		response.setMessage("Invalid Credentials");
		response.setTimestamp(LocalDateTime.now());

		// Get all validation errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		// Add errors to the response map
		response.setFieldErrors(errors);

		return new ResponseEntity<>(response, headers, status);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ExceptionDetails> handlesUserNotFoundException(
			InvalidCredentialsException ex) {
		ExceptionDetails response = new ExceptionDetails(ex.getMessage(), LocalDateTime.now(),
				Collections.singletonList(ex.getMessage()));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ExceptionDetails> handlesInvalidTokenException(InvalidTokenException ex) {
		ExceptionDetails response = new ExceptionDetails(ex.getMessage(), LocalDateTime.now(),
				Collections.singletonList(ex.getMessage()));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}

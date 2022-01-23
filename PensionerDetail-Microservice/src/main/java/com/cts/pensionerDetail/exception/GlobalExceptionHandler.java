package com.cts.pensionerDetail.exception;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionDetails> handlesUserNotFoundException(NotFoundException ex) {
		ExceptionDetails response = new ExceptionDetails(ex.getMessage(), LocalDateTime.now(),
				Collections.singletonList(ex.getMessage()));
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

}

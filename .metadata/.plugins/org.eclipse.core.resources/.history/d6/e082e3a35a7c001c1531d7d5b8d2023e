package com.cts.process.pension.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionDetails {

	private String message;
	private LocalDateTime timestamp;

	public ExceptionDetails(String message) {
		this.timestamp = LocalDateTime.now();
		this.message = message;
	}
	@JsonInclude(Include.NON_NULL)
	private List<String> fieldErrors;
}

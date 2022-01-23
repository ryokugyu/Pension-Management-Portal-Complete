package com.cts.pensionerDetail.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionDetails {

	private String message;
	private LocalDateTime timestamp;
	@JsonInclude(Include.NON_NULL)
	private List<String> fieldErrors;
}

package com.cts.auth.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserRequest {

	@NotBlank(message = "EMPTY STRING NOT ALLOWED")
	@Size(min = 4, max = 10, message = "Username length should be from 4 to 10 characters")
	private String username;

	@NotBlank(message = "EMPTY STRING NOT ALLOWED")
	private String password;
}
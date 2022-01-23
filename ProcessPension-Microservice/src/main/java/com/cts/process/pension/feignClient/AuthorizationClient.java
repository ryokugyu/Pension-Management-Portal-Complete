package com.cts.process.pension.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("AUTHORIZATION-MICROSERVICE")
public interface AuthorizationClient {

	@GetMapping("/authorize")
	public boolean authorizeTheRequest(
			@RequestHeader(name = "Authorization", required = true) String requestTokenHeader);

}
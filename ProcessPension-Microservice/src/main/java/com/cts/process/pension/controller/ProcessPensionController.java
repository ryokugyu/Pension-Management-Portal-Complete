package com.cts.process.pension.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cts.process.pension.exception.InvalidTokenException;
import com.cts.process.pension.feignClient.AuthorizationClient;
import com.cts.process.pension.model.PensionDetail;
import com.cts.process.pension.model.PensionerInput;
import com.cts.process.pension.service.ProcessPensionServiceImpl;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin
public class ProcessPensionController {

	@Autowired
	ProcessPensionServiceImpl processPensionService;

	@Autowired
	AuthorizationClient authorizationClient;

	@PostMapping("/ProcessPension")
	public ResponseEntity<PensionDetail> getPensionDetails(
			@RequestHeader(name = "Authorization", required = true) String requestTokenHeader,
			@RequestBody @Valid PensionerInput pensionerInput) {
		log.info("EXECUTION START ->  getPensionDetails()");
		if (!authorizationClient.authorizeTheRequest(requestTokenHeader)) {
			throw new InvalidTokenException("ACCESS RESTRICTED");
		}

		log.info("EXECUTION END -> getPensionDetails()");
		return new ResponseEntity<>(processPensionService.getPensionDetails(pensionerInput), HttpStatus.OK);
	}
}
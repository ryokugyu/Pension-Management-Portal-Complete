package com.cts.pensionerDetail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cts.pensionerDetail.model.PensionerDetails;
import com.cts.pensionerDetail.service.PensionerDetailServiceImpl;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
@CrossOrigin
public class PensionerDetailsController {

	@Autowired
	private PensionerDetailServiceImpl pensionerDetailService;

	@GetMapping("/pensionerDetailByAadhaar/{aadhaarNumber}")
	public PensionerDetails getPensionerDetailByAadhaar(@PathVariable String aadhaarNumber) {
		log.info("EXECUTION START -> getPensionerDetailByAadhaar()");
		return pensionerDetailService.getPensionerDetailByAadhaarNumber(aadhaarNumber);
	}

}

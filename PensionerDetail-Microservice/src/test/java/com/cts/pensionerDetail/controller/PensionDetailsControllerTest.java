package com.cts.pensionerDetail.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
//import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.pensionerDetail.exception.NotFoundException;
import com.cts.pensionerDetail.model.BankDetails;
import com.cts.pensionerDetail.model.PensionerDetails;
import com.cts.pensionerDetail.service.PensionerDetailServiceImpl;
import com.cts.pensionerDetail.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest
@Slf4j
class PensionDetailsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PensionerDetailServiceImpl pensionerDetailService;

	@Test
	@DisplayName(value = "Method to test whether Aadhaar Card Number present or not in CSV")
	void test_fetchPensionerDetails_aadhaarCardPresent() throws Exception {

		log.info("TEST EXECUTION START - test_fetchPensionerDetails_aadhaarCardPresent()");
		final String aadhaarCardNumber = "848766419593";
		PensionerDetails pensionerDetail = new PensionerDetails("Caleb", DateUtil.parseDate("21-04-2012"), "K94ZHJ8ZXC",
				36004, 5919, "self", new BankDetails("New Edge Bank System", 61390268, "public"));
		when(pensionerDetailService.getPensionerDetailByAadhaarNumber(aadhaarCardNumber)).thenReturn(pensionerDetail);
		mockMvc.perform(
				get("/pensionerDetailByAadhaar/{aadhaarNumber}", aadhaarCardNumber).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", Matchers.equalTo("Caleb")))
				.andExpect(jsonPath("$.pan", Matchers.equalTo("K94ZHJ8ZXC")))
				.andExpect(jsonPath("$.dateOfBirth", Matchers.equalTo("2012-04-21")))
				.andExpect(jsonPath("$.bank.accountNumber", Matchers.equalTo(61390268)));
		log.info("TEST EXECUTION END - test_fetchPensionerDetails_aadhaarCardPresent()");
	}

	@Test
	@DisplayName(value = "Method to test whether Aadhaar Card Number present or not in CSV")
	void test_fetchPensionerDetails_aadhaarCardNotPresent() throws Exception {
		log.info("TEST EXECUTION START - test_fetchPensionerDetails_aadhaarCardNotPresent()");
		final String aadhaarNumber = "999999999999";

		when(pensionerDetailService.getPensionerDetailByAadhaarNumber(ArgumentMatchers.any()))
				.thenThrow(new NotFoundException("AADHAAR CARD NUMBER NOT FOUND"));

		mockMvc.perform(
				get("/pensionerDetailByAadhaar/{aadhaarNumber}", aadhaarNumber).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", Matchers.equalTo("AADHAAR CARD NUMBER NOT FOUND")));
		log.info("TEST EXECUTION END - test_fetchPensionerDetails_aadhaarCardNotPresent()");
	}

}

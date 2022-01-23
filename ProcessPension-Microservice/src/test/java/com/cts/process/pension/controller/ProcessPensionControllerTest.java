package com.cts.process.pension.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.process.pension.exception.ExceptionDetails;
import com.cts.process.pension.exception.NotFoundException;
import com.cts.process.pension.feignClient.AuthorizationClient;
import com.cts.process.pension.feignClient.PensionerDetailsClient;
import com.cts.process.pension.model.PensionDetail;
import com.cts.process.pension.model.PensionerInput;
import com.cts.process.pension.service.ProcessPensionServiceImpl;
import com.cts.process.pension.util.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Request;
import feign.FeignException;
import feign.Request.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@WebMvcTest(ProcessPensionController.class)
@Slf4j
class ProcessPensionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthorizationClient authorizationClient;

	@MockBean
	private PensionerDetailsClient pensionerDetailsClient;

	@MockBean
	private ProcessPensionServiceImpl processPensionService;

	@Autowired
	private ObjectMapper objectMapper;

	private PensionerInput validPensionerInput;
	private PensionerInput invalidPensionerInput;
	private PensionDetail pensionDetail;

	@BeforeEach
	void setup() throws ParseException {

		// valid PensionerInput
		validPensionerInput = new PensionerInput("Himanshu", DateUtil.parseDate("11-06-1996"), "ALPCM2286P",
				"525263634189", "self");

		// invalid PensionerInput
		invalidPensionerInput = new PensionerInput("Himanshu", DateUtil.parseDate("11-06-1996"), "ALPCM2286P", "",
				"self");

		// correct PensionDetails
		pensionDetail = new PensionDetail("Himanshu", DateUtil.parseDate("11-06-1996"), "ALPCM2286P", "family", 50000,
				550);

		// mock authorization microservice response
		when(authorizationClient.authorizeTheRequest(ArgumentMatchers.anyString())).thenReturn(true);

	}

	@Test
	@DisplayName(value = "Method to test response after sending post request with valid data to /ProcessPension")
	void test_GetPensionDetailsWithValidInput() throws Exception {
		log.info("TEST EXECUTION START - test_GetPensionDetailsWithValidInput()");
		// mock disbursePensionSerive response
		when(processPensionService.getPensionDetails(ArgumentMatchers.any())).thenReturn(pensionDetail);

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
				.header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
				.content(objectMapper.writeValueAsString(validPensionerInput)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.pensionAmount", Matchers.equalTo(50000.0)));
		log.info("TEST EXECUTION END - test_GetPensionDetailsWithValidInput()");
	}

	@Test
	@DisplayName(value = "Method to test response after sending post request with invalid token to /ProcessPension")
	void test_GetPensionDetailsWithInvalidToken() throws Exception {
		log.info("TEST EXECUTION START - test_GetPensionDetailsWithInvalidToken()");
		// mock authorization microservice response for invalid token
		when(authorizationClient.authorizeTheRequest(ArgumentMatchers.anyString())).thenReturn(false);

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(validPensionerInput)).accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "user1")).andExpect(status().isForbidden());
		log.info("TEST EXECUTION END - test_GetPensionDetailsWithInvalidToken()");
	}

	@Test
	@DisplayName(value = "Method to test Global Handler")
	void test_GlobalExceptions() throws Exception {
		log.info("TEST EXECUTION START - test_GlobalExceptions()");
		final String errorMessage = "Invalid Credentials";

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
				.content(objectMapper.writeValueAsString(invalidPensionerInput)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", Matchers.equalTo(errorMessage)));
		log.info("TEST EXECUTION END - test_GlobalExceptions()");
	}

	@Test
	@DisplayName(value = "Method to test response after sending post request with invalid data to /ProcessPension")
	void test_PensionInputwithInvalidInput() throws Exception {
		log.info("TEST EXECUTION START - test_PensionInputwithInvalidInput()");
		// mock processPensionService response
		when(processPensionService.getPensionDetails(ArgumentMatchers.any()))
				.thenThrow(new NotFoundException("Details entered are incorrect"));

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
				.header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
				.content(objectMapper.writeValueAsString(validPensionerInput)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message", Matchers.equalTo("Details entered are incorrect")));
		log.info("TEST EXECUTION END - test_PensionInputwithInvalidInput()");
	}

	@Test
	@DisplayName(value = "Method to test response when feign client returns valid error response")
	void test_DisbursePensionwithValidFeignResponse() throws JsonProcessingException, Exception {
		log.info("TEST EXECUTION START - test_DisbursePensionwithValidFeignResponse()");
		// mock processPensionService getPensionDetails to throw FeignException
		when(processPensionService.getPensionDetails(ArgumentMatchers.any())).thenThrow(new FeignException.BadRequest(
				"Service is offline", Request.create(HttpMethod.GET, "", Collections.emptyMap(), null, null, null),
				objectMapper.writeValueAsBytes(new ExceptionDetails("Internal Server Error"))));

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(validPensionerInput)).accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", Matchers.equalTo("Internal Server Error")));
		log.info("TEST EXECUTION END - test_DisbursePensionwithValidFeignResponse()");

	}

	@Test
	@DisplayName(value = "Method to test response when feign client returns invalid error response")
	void test_PensionInputwithInvalidFeignResponse() throws JsonProcessingException, Exception {
		log.info("TEST EXECUTION START - test_PensionInputwithInvalidFeignResponse()");
		// mock processPensionService getPensionDetails to throw FeignException
		when(processPensionService.getPensionDetails(ArgumentMatchers.any())).thenThrow(new FeignException.BadRequest(
				"Invalid Response", Request.create(HttpMethod.GET, "", Collections.emptyMap(), null, null, null),
				"Unknown error response".getBytes()));

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(validPensionerInput)).accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", Matchers.equalTo("Unknown error response")));
		log.info("TEST EXECUTION END - test_PensionInputwithInvalidFeignResponse()");
	}

	@Test
	@DisplayName(value = "Method to test response when feign client returns empty message response")
	void test_PensionInputwithEmptyFeignResponse() throws JsonProcessingException, Exception {
		log.info("TEST EXECUTION START - test_PensionInputwithEmptyFeignResponse()");
		// mock processPensionService getPensionDetails to throw FeignException
		when(processPensionService.getPensionDetails(ArgumentMatchers.any()))
				.thenThrow(new FeignException.BadRequest("Invalid Response",
						Request.create(HttpMethod.GET, "", Collections.emptyMap(), null, null, null), "".getBytes()));

		// performing test
		mockMvc.perform(post("/ProcessPension").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(validPensionerInput)).accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", Matchers.equalTo("Invalid Request")));
		log.info("TEST EXECUTION END - test_PensionInputwithEmptyFeignResponse()");
	}
}

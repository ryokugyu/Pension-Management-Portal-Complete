package com.cts.pensionerDetail.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.DisplayName;
//import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import com.cts.pensionerDetail.exception.NotFoundException;
import com.cts.pensionerDetail.model.BankDetails;
import com.cts.pensionerDetail.model.PensionerDetails;
import com.cts.pensionerDetail.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class PensionDetailServiceTest {

	@Autowired
	PensionerDetailService pensionerDetailService;

	@Test
	@DisplayName(value = "Method to test Pensioner Detail Service Object is null or not")
	void test_PensionDetailServiceObject() {
		assertNotNull(pensionerDetailService);
	}

	@Test
	@DisplayName(value = "Method to test whether details provided by valid aadhaar card number is correct or not")
	void test_validateCorrectAadhaarCardDetails()
			throws IOException, NotFoundException, NumberFormatException, ParseException, NullPointerException {

		log.info("TEST EXECUTION START - test_fetchPensionerDetails_aadhaarCardPresent()");
		final String aadhaarCardNumber = "982521289534";
		PensionerDetails pensionerDetail = new PensionerDetails("982521289534", DateUtil.parseDate("11-02-2009"),
				"XZWE3HWLXR", 32940, 6252, "self", new BankDetails("Goldward Financial Group", 88980359, "public"));
		assertEquals(pensionerDetailService.getPensionerDetailByAadhaarNumber(aadhaarCardNumber).getPan(),
				pensionerDetail.getPan());
		assertEquals(pensionerDetailService.getPensionerDetailByAadhaarNumber(aadhaarCardNumber).getBank()
				.getAccountNumber(), pensionerDetail.getBank().getAccountNumber());
		log.info("TEST EXECUTION END - test_fetchPensionerDetails_aadhaarCardPresent()");
	}

	@Test
	@DisplayName(value = "Method to test whether details provided by invalid aadhaar card number or not")
	void test_validateInCorrectAadhaarCardDetails() {

		log.info("TEST EXECUTION START - test_fetchPensionerDetails_aadhaarCardPresent()");
		final String aadhaarCardNumber = "12345678";
		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> pensionerDetailService.getPensionerDetailByAadhaarNumber(aadhaarCardNumber));
		assertEquals(exception.getMessage(), "AADHAAR CARD NUMBER NOT FOUND");
		assertNotNull(exception);
		log.info("TEST EXECUTION END - test_fetchPensionerDetails_aadhaarCardPresent()");
	}
}

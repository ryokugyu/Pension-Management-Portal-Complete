package com.cts.process.pension.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cts.process.pension.model.PensionAmountDetail;
import com.cts.process.pension.model.PensionerInput;
import com.cts.process.pension.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class PensionDetailsRepositoryTests {

	@Autowired
	private PensionDetailsRepository pensionDetailsRepository;

	@Autowired
	private PensionerDetailsRepository pensionerDetailsRepository;

	@Test
	@DisplayName(value = "Method to test the save() functionality for pension details")
	void test_save() {
		log.info("TEST EXECUTION START - test_save()");

		PensionAmountDetail pensionAmountDetail = new PensionAmountDetail();
		pensionAmountDetail.setAadhaarNumber("801993153377");
		pensionAmountDetail.setBankServiceCharge(500.00);
		pensionAmountDetail.setPensionAmount(21600.00);
		pensionAmountDetail.setFinalAmount(21000.00);

		PensionAmountDetail savedDetails = pensionDetailsRepository.save(pensionAmountDetail);
		assertEquals(savedDetails.getAadhaarNumber(), pensionAmountDetail.getAadhaarNumber());
		assertEquals(savedDetails.getBankServiceCharge(), pensionAmountDetail.getBankServiceCharge());
		assertEquals(savedDetails.getPensionAmount(), pensionAmountDetail.getPensionAmount());
		assertEquals(savedDetails.getFinalAmount(), pensionAmountDetail.getFinalAmount());

		assertNotNull(savedDetails);
		log.info("TEST EXECUTION END - test_save()");
	}

	@Test
	@DisplayName(value = "Method to test the save() functionality for pensioner details")
	void test_savePensionerDetail() throws ParseException {
		log.info("TEST EXECUTION START - test_savePensionerDetail()");

		PensionerInput pensionerInput = new PensionerInput("Himanshu", DateUtil.parseDate("11-06-1996"), "Q3ERR3ELLH",
				"873006088777", "family");

		PensionerInput savedDetails = pensionerDetailsRepository.save(pensionerInput);
		assertEquals(savedDetails.getAadhaarNumber(), pensionerInput.getAadhaarNumber());
		assertNotNull(savedDetails);

		log.info("TEST EXECUTION END - test_savePensionerDetail()");
	}
}

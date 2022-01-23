package com.cts.process.pension.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.process.pension.exception.NotFoundException;
//import com.cts.process.pension.feignClient.PensionDisbursementClient;
import com.cts.process.pension.feignClient.PensionerDetailsClient;
//import com.cts.process.pension.model.PensionAmountDetail;
import com.cts.process.pension.model.PensionDetail;
import com.cts.process.pension.model.PensionerDetail;
import com.cts.process.pension.model.PensionerInput;
//import com.cts.process.pension.model.ProcessPensionInput;
//import com.cts.process.pension.model.ProcessPensionResponse;
//import com.cts.process.pension.repository.PensionDetailsRepository;
import com.cts.process.pension.repository.PensionerDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessPensionServiceImpl implements ProcessPensionService {

	@Autowired
	private PensionerDetailsClient pensionerDetailClient;

//	@Autowired
//	private PensionDisbursementClient pensionDisbursementClient;

//	@Autowired
//	private PensionDetailsRepository pensionDetailsRepository;

	@Autowired
	private PensionerDetailsRepository pensionerDetailsRepository;

	@Override
	public PensionDetail getPensionDetails(PensionerInput pensionerInput) {

		// get the pensioner details from the detail micro-service
		PensionerDetail pensionerDetail = pensionerDetailClient
				.getPensionerDetailByAadhaar(pensionerInput.getAadhaarNumber());

		log.info("Details found");

		if (checkdetails(pensionerInput, pensionerDetail)) {
			pensionerDetailsRepository.save(pensionerInput);

			return calculatePensionAmount(pensionerDetail);
		} else {
			throw new NotFoundException("INCORRECT DETAILS");
		}
	}

	/*
	 * To update Bank Charges here, we have to add bank charges and PensionDetail
	 */
	@Override
	public PensionDetail calculatePensionAmount(PensionerDetail pensionDetail) {
		double pensionAmount = 0;
		int bankCharges = 0;
		String bankType = pensionDetail.getBank().getBankType();
		if (bankType.equalsIgnoreCase("public")) {
			bankCharges = 500;
		} else if (bankType.equalsIgnoreCase("private")) {
			bankCharges = 550;
		}
		if (pensionDetail.getPensionType().equalsIgnoreCase("self"))
			pensionAmount = (pensionDetail.getSalary() * 0.8 + pensionDetail.getAllowance());
		else if (pensionDetail.getPensionType().equalsIgnoreCase("family"))
			pensionAmount = (pensionDetail.getSalary() * 0.5 + pensionDetail.getAllowance());
		return new PensionDetail(pensionDetail.getName(), pensionDetail.getDateOfBirth(), pensionDetail.getPan(),
				pensionDetail.getPensionType(), pensionAmount, bankCharges);
	}

	@Override
	public boolean checkdetails(PensionerInput pensionerInput, PensionerDetail pensionerDetail) {
		return (pensionerInput.getName().equalsIgnoreCase(pensionerDetail.getName())
				&& (pensionerInput.getDateOfBirth().compareTo(pensionerDetail.getDateOfBirth()) == 0)
				&& pensionerInput.getPan().equalsIgnoreCase(pensionerDetail.getPan())
				&& pensionerInput.getPensionType().equalsIgnoreCase(pensionerDetail.getPensionType()));
	}

//	@Override
//	public ProcessPensionResponse processPension(String requestTokenHeader, ProcessPensionInput processPensionInput) {
//		int hitCounter = 0;
//		ProcessPensionResponse pensionResponse = pensionDisbursementClient.disbursePension(requestTokenHeader,
//				processPensionInput);
//
//		// retry the disbursement 2 more times if status code is 21
//		while (pensionResponse.getProcessPensionStatusCode() == 21 && hitCounter < 2) {
//			log.debug("Hitting the disbursement service again...");
//			pensionResponse = pensionDisbursementClient.disbursePension(requestTokenHeader, processPensionInput);
//			++hitCounter;
//		}
//
//		// if response is 10, then we store the amount details in the database
//		pensionDetailsRepository.save(new PensionAmountDetail(processPensionInput.getAadhaarNumber(),
//				processPensionInput.getPensionAmount(), processPensionInput.getBankServiceCharge(),
//				processPensionInput.getPensionAmount() - processPensionInput.getBankServiceCharge()));
//
//		return pensionResponse;
//	}
}
package com.cts.process.pension.service;

import com.cts.process.pension.model.PensionDetail;
import com.cts.process.pension.model.PensionerDetail;
import com.cts.process.pension.model.PensionerInput;

public interface ProcessPensionService {

	public PensionDetail getPensionDetails(PensionerInput pensionerInput);

	public PensionDetail calculatePensionAmount(PensionerDetail pensionDetail);

	public boolean checkdetails(PensionerInput pensionerInput, PensionerDetail pensionerDetail);

}

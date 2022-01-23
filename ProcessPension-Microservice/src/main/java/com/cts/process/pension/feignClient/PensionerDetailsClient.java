package com.cts.process.pension.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.process.pension.model.PensionerDetail;

@FeignClient("PENSIONER-DETAIL-MICROSERVICE")
public interface PensionerDetailsClient {
	@GetMapping("/pensionerDetailByAadhaar/{aadhaarNumber}")
	public PensionerDetail getPensionerDetailByAadhaar(@PathVariable String aadhaarNumber);
}
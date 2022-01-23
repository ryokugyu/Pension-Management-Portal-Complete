package com.cts.pensionerDetail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class BankDetails {

	private String bankName;
	private long accountNumber;
	private String bankType;
}

package com.cts.process.pension.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Entity
public class PensionAmountDetail {

	@Id
	private String aadhaarNumber;

	@Column
	private Double pensionAmount;

	@Column
	private Double bankServiceCharge;

	@Column
	private Double finalAmount;
}

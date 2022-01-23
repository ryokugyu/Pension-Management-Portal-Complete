package com.cts.process.pension.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DateUtil {

	public static Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}
}

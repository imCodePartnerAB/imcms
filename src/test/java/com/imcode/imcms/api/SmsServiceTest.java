package com.imcode.imcms.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SmsServiceTest extends WebAppSpringTestConfig {

	@Autowired
	private SmsService smsService;

	@Test
	public void sendSmsWithEmptyData_Expect_CorrectExceptions() {
		// message empty or null
		Assertions.assertThrows(IllegalArgumentException.class, () -> smsService.sendSms("", "464654846"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> smsService.sendSms(null, "464654846"));
		// recipient empty or null
		Assertions.assertThrows(IllegalArgumentException.class, () -> smsService.sendSms("message", ""));
		Assertions.assertThrows(IllegalArgumentException.class, () -> smsService.sendSms("message", null));
		// all empty or null
		Assertions.assertThrows(IllegalArgumentException.class, () -> smsService.sendSms("", ""));
		Assertions.assertThrows(IllegalArgumentException.class, () -> smsService.sendSms(null, null));
	}

	@Test
	public void sendSmsWithIncorrectPhoneNumber_Expect_SmsNotSent() {
		Assertions.assertFalse(smsService.sendSms("MESSAGE", "12"));
	}

}

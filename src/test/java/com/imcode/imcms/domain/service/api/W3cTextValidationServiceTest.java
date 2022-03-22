package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.TextValidationResult;
import com.imcode.imcms.domain.service.TextValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class W3cTextValidationServiceTest extends WebAppSpringTestConfig {

	@Autowired
	private TextValidationService textValidationService;

	@Test
	public void validateText_When_TextIsEmpty_Expect_ValidNoErrorsNoWarnings() throws Exception {

		final TextValidationResult validationResult = textValidationService.validateText("");

		assertTrue(validationResult.isValid());
		assertTrue(validationResult.getErrors().isEmpty());
		assertTrue(validationResult.getWarnings().isEmpty());
	}

	@Test
	public void validateText_When_SomeHtmlTextIsPresent_Expect_ValidNoErrorsNoWarnings() throws Exception {

		final String content = "<div class=\"test-class\"><span>test</span><p>text</p></div>";
		final TextValidationResult validationResult = textValidationService.validateText(content);

		assertTrue(validationResult.isValid());
		assertTrue(validationResult.getErrors().isEmpty());
		assertTrue(validationResult.getWarnings().isEmpty());
	}

	@Test
	public void validateText_When_SomeBadHtmlTextIsPresent_Expect_InvalidWithTwoErrorsNoWarnings() throws Exception {

		final String content = "<div>";
		final TextValidationResult validationResult = textValidationService.validateText(content);

		assertFalse(validationResult.isValid());
		assertFalse(validationResult.getErrors().isEmpty());
		assertEquals(validationResult.getErrors().size(), 2); // 2 is an amount of errors, empirically received
		assertTrue(validationResult.getWarnings().isEmpty());
	}
}

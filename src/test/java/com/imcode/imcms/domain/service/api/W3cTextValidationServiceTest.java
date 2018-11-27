package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.TextValidationResult;
import com.imcode.imcms.domain.dto.ValidationData;
import com.imcode.imcms.domain.service.TextValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class W3cTextValidationServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private TextValidationService textValidationService;

    @Test
    public void validateText_When_TextIsEmpty_Expect_ValidNoErrorsNoWarnings() throws Exception {

        final TextValidationResult validationResult = textValidationService.validateText("");
        final ValidationData data = validationResult.getData();

        assertTrue(validationResult.isValid());
        assertTrue(data.getErrors().isEmpty());
        assertTrue(data.getWarnings().isEmpty());

    }

    @Test
    public void validateText_When_SomeHtmlTextIsPresent_Expect_ValidNoErrorsNoWarnings() throws Exception {

        final String content = "<div class=\"test-class\"><span>test</span><p>text</p></div>";
        final TextValidationResult validationResult = textValidationService.validateText(content);
        final ValidationData data = validationResult.getData();

        assertTrue(validationResult.isValid());
        assertTrue(data.getErrors().isEmpty());
        assertTrue(data.getWarnings().isEmpty());

    }

    @Test
    public void validateText_When_SomeBadHtmlTextIsPresent_Expect_InvalidWithTwoErrorsNoWarnings() throws Exception {

        final String content = "<div>";
        final TextValidationResult validationResult = textValidationService.validateText(content);
        final ValidationData data = validationResult.getData();

        assertFalse(validationResult.isValid());
        assertFalse(data.getErrors().isEmpty());
        assertEquals(data.getErrors().size(), 2); // 2 is an amount of errors, empirically received
        assertTrue(data.getWarnings().isEmpty());

    }
}

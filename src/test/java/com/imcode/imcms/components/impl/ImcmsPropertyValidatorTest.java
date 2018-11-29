package com.imcode.imcms.components.impl;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.exception.ImcmsPropertiesValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ValidationException;
import java.util.Properties;

import static com.imcode.imcms.components.impl.ImcmsPropertyValidator.REQUIRED_PROPERTIES;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.04.18.
 */

public class ImcmsPropertyValidatorTest extends WebAppSpringTestConfig {

    private Properties properties;
    private final ImcmsPropertyValidator propertyValidator = new ImcmsPropertyValidator();

    @BeforeEach
    public void setUp() {
        properties = new Properties();
    }

    @Test
    public void validate_When_PropertiesEmpty_Expect_Exception() {
        assertThrows(ImcmsPropertiesValidationException.class,
                () -> propertyValidator.validate(properties));
    }

    @Test
    public void validate_When_AllRequiredPropertiesAreSetNonEmpty_Expect_NoException() {
        for (String requiredProperty : REQUIRED_PROPERTIES) {
            properties.setProperty(requiredProperty, "any_value");
        }

        propertyValidator.validate(properties);
    }

    @Test
    public void validate_When_PropertiesAreEmpty_Expect_CorrectExceptionMessage() {
        try {
            propertyValidator.validate(properties);
            fail("Expected exception wasn't thrown!");

        } catch (ValidationException e) {
            final String message = e.getMessage();

            for (String requiredProperty : REQUIRED_PROPERTIES) {
                assertTrue(message.contains(requiredProperty));
            }
        }
    }
}

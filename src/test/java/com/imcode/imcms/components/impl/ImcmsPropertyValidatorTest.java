package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.exception.ImcmsPropertiesValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.validation.ValidationException;
import java.util.Properties;

import static com.imcode.imcms.components.impl.ImcmsPropertyValidator.REQUIRED_PROPERTIES;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.04.18.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ImcmsPropertyValidatorTest {

    private Properties properties;
    private ImcmsPropertyValidator propertyValidator = new ImcmsPropertyValidator();

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
    }

    @Test(expected = ImcmsPropertiesValidationException.class)
    public void validate_When_PropertiesEmpty_Expect_Exception() {
        propertyValidator.validate(properties);
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

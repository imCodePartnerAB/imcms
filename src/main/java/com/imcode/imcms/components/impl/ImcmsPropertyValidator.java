package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.Validator;
import com.imcode.imcms.components.exception.ImcmsPropertiesValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Properties validator, should fail ImCMS startup if some required properties
 * is not set.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.04.18.
 */
@Component
class ImcmsPropertyValidator implements Validator<Properties> {

    final static String[] REQUIRED_PROPERTIES = {
            "DefaultLanguage",
            "AvailableLanguages",
            "JdbcDriver",
            "JdbcUrl",
            "User",
            "Password",
    };

    @Override
    public void validate(Properties validateMe) throws ValidationException {
        final String missingProperties = Stream.of(REQUIRED_PROPERTIES)
                .filter(propertyName -> StringUtils.isBlank(validateMe.getProperty(propertyName)))
                .collect(Collectors.joining(", "));

        if (missingProperties.isEmpty()) {
            return;
        }

        throw new ImcmsPropertiesValidationException("Missing required properties: " + missingProperties);
    }

}

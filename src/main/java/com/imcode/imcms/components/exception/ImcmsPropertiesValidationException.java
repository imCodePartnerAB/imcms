package com.imcode.imcms.components.exception;

import jakarta.validation.ValidationException;

/**
 * Indicates that something wrong with required imcms properties.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.04.18.
 */
public class ImcmsPropertiesValidationException extends ValidationException {

    private static final long serialVersionUID = -8063618949892864259L;

    public ImcmsPropertiesValidationException(String message) {
        super(message);
    }
}

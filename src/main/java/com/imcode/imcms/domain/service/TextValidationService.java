package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TextValidationResult;

import java.io.IOException;

/**
 * Service for text validation.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
public interface TextValidationService {

    /**
     * Provides simple validation api.
     *
     * @param content Text, that should be validated
     * @return validation result
     * @throws IOException if content couldn't be validated
     */
    TextValidationResult validateText(String content) throws IOException;

}

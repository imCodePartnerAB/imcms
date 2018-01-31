package com.imcode.imcms.domain.dto;

import com.jcabi.w3c.ValidationResponse;
import lombok.Data;

/**
 * Represents text validation result.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Data
public class TextValidationResult {

    private boolean valid;
    private String message;
    private ValidationData data;

    public TextValidationResult(ValidationResponse response) {
        this.valid = response.valid();
        this.message = response.toString();
        this.data = new ValidationData(response);
    }
}

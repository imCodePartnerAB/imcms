package com.imcode.imcms.domain.dto;

import com.jcabi.w3c.Defect;
import com.jcabi.w3c.ValidationResponse;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class holds errors and warnings data as text validation result.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Data
public class ValidationData {

    private Set<TextValidationDefect> errors;
    private Set<TextValidationDefect> warnings;

    ValidationData(ValidationResponse response) {
        this.errors = transformToTextValidationDefect(response.errors());
        this.warnings = transformToTextValidationDefect(response.warnings());
    }

    private Set<TextValidationDefect> transformToTextValidationDefect(Set<Defect> defects) {
        return defects.stream().map(TextValidationDefect::new).collect(Collectors.toSet());
    }
}

package com.imcode.imcms.domain.dto;

import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents text validation result.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Data
public class TextValidationResult {

	private boolean valid;
	private Set<TextValidationDefect> errors;
	private Set<TextValidationDefect> warnings;

	public TextValidationResult(ValidationData data) {
		this.valid = data.getDefects().size() == 0;
		this.errors = data.getDefects().stream().filter(defect -> defect.getType().contains("error")).collect(Collectors.toSet());
		this.warnings = data.getDefects().stream().filter(defect -> defect.getType().contains("info")).collect(Collectors.toSet());
	}
}
